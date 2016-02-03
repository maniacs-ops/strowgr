package main

import (
	"flag"
	"fmt"
	"github.com/bitly/go-nsq"
	"log"
	"net/http"
	"encoding/json"
	"os"
	"bytes"
	"github.com/BurntSushi/toml"
	"gitlab.socrate.vsct.fr/dt/haaas"
)

var (
	ip = flag.String("ip", "4.3.2.1", "Node ip address")
	configFile = flag.String("config", "haaas.conf", "Configuration file")
	config = nsq.NewConfig()
	properties haaas.Config
	daemon *haaas.Daemon
)

func main() {

	flag.Parse()

	if _, err := toml.DecodeFile(*configFile, &properties); err != nil {
		log.Fatal(err)
		os.Exit(1)
	}
	properties.IpAddr = *ip
	len := len(properties.HapHome)
	if properties.HapHome[len - 1] == '/' {
		properties.HapHome = properties.HapHome[:len - 1 ]
	}

	daemon = haaas.NewDaemon(&properties)

	log.Printf("Starting haaasd for cluster %v", properties.NodeId())

	startTryUpdateConsumer()
	startUpdateConsumer()

	starRestAPI()
}

func check(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func startTryUpdateConsumer() {
	tryUpdateConsumer, _ := nsq.NewConsumer(fmt.Sprintf("try_update_%s", properties.ClusterId), properties.NodeId(), config)

	tryUpdateConsumer.AddHandler(nsq.HandlerFunc(func(message *nsq.Message) error {
		defer message.Finish()

		isSlave, err := daemon.IsSlave()
		if err != nil {
			return err
		}

		if isSlave {
			log.Printf("Receive try_update event %s", message.Body)
			data, err := bodyToDatas(message.Body)
			check(err)

			hap := haaas.NewHaproxy(&properties, data.Application, data.Platform)
			err = hap.ApplyConfiguration(data)
			if err == nil {
				publishMessage("update_", data)
			}else {
				log.Fatal(err)
			}
		}

		return nil
	}))

	err := tryUpdateConsumer.ConnectToNSQLookupd(properties.LookupdAddr)
	if err != nil {
		log.Panic("Could not connect")
	}
}


func startUpdateConsumer() {
	updateConsumer, err := nsq.NewConsumer("update_" + properties.ClusterId, properties.NodeId(), config)
	check(err)

	updateConsumer.AddHandler(nsq.HandlerFunc(func(message *nsq.Message) error {
		defer message.Finish()
		isMaster, err := daemon.IsMaster()
		if err != nil {
			return err
		}

		if isMaster {
			log.Printf("Receive update event %s", message.Body)
			data, err := bodyToDatas(message.Body)
			check(err)

			hap := haaas.NewHaproxy(&properties, data.Application, data.Platform)
			err = hap.ApplyConfiguration(data)
			if err == nil {
				publishMessage("updated_", map[string]string{"application" : data.Application, "platform": data.Platform})
			}else {
				log.Fatal(err)
			}
		}

		return nil
	}))

	err = updateConsumer.ConnectToNSQLookupd(properties.LookupdAddr)
	if err != nil {
		log.Panic("Could not connect")
	}
}

func bodyToDatas(jsonStream []byte) (haaas.EventMessage, error) {
	dec := json.NewDecoder(bytes.NewReader(jsonStream))
	var message haaas.EventMessage
	dec.Decode(&message)
	return message, nil
}

func starRestAPI() {
	http.HandleFunc("/real-ip", func(writer http.ResponseWriter, request *http.Request) {
		log.Printf("GET /real-ip")
		fmt.Fprintf(writer, "%s\n", *ip)
	})
	log.Printf("Start listening on port %d", properties.Port)
	if err := http.ListenAndServe(fmt.Sprintf(":%d", properties.Port), nil); err != nil {
		log.Fatal("ListenAndServe:", err)
	}
}

func publishMessage(topic_prefix string, data interface{}) error {
	producer, _ := nsq.NewProducer("floradora:50150", config)
	jsonMsg, _ := json.Marshal(data)
	topic := topic_prefix + properties.ClusterId
	log.Printf("Publish to %s : %s", topic, jsonMsg)
	return producer.Publish(topic, []byte(jsonMsg))
}

