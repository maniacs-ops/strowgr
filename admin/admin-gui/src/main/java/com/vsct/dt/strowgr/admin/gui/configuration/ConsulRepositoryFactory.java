package com.vsct.dt.strowgr.admin.gui.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsct.dt.strowgr.admin.repository.consul.ConsulRepository;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by william_montaz on 15/02/2016.
 */
public class ConsulRepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulRepositoryFactory.class);

    @NotEmpty
    private String host;

    @Min(1)
    @Max(65535)
    private int port;

    @JsonProperty(value = "minGeneratedPort")
    private int minGeneratedPort = 32000;

    @JsonProperty("maxGeneratedPort")
    private int maxGeneratedPort = 64000;

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public int getMinGeneratedPort() {
        return minGeneratedPort;
    }

    @JsonProperty
    public void setMinGeneratedPort(int minGeneratedPort) {
        this.minGeneratedPort = minGeneratedPort;
    }

    @JsonProperty
    public int getMaxGeneratedPort() {
        return maxGeneratedPort;
    }

    @JsonProperty
    public void setMaxGeneratedPort(int maxGeneratedPort) {
        this.maxGeneratedPort = maxGeneratedPort;
    }

    public ConsulRepository build(Environment environment) {
        ConsulRepository repository = new ConsulRepository(getHost(), getPort(), getMinGeneratedPort(), getMaxGeneratedPort());
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
            }

            @Override
            public void stop() throws Exception {
                LOGGER.info("Shutting down consul repository client");
                repository.shutdown();
            }
        });
        return repository;
    }
}