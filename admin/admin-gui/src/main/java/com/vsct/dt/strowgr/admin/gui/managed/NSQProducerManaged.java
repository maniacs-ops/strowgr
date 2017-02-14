/*
 * Copyright (C) 2016 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vsct.dt.strowgr.admin.gui.managed;

import fr.vsct.dt.nsq.NSQProducer;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NSQ client manager by Dropwizard.
 */
public class NSQProducerManaged implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(NSQProducerManaged.class);

    private final NSQProducer nsqProducer;

    public NSQProducerManaged(NSQProducer nsqProducer) {
        this.nsqProducer = nsqProducer;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("start NSQProducer");
        this.nsqProducer.start();
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stop NSQProducer");
        this.nsqProducer.shutdown();
    }
}
