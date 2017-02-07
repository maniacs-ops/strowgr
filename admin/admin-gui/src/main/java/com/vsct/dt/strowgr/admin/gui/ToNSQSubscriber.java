/*
 *  Copyright (C) 2016 VSCT
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.vsct.dt.strowgr.admin.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vsct.dt.strowgr.admin.core.configuration.EntryPoint;
import com.vsct.dt.strowgr.admin.core.event.out.CommitRequestedEvent;
import com.vsct.dt.strowgr.admin.core.event.out.DeleteEntryPointEvent;
import com.vsct.dt.strowgr.admin.nsq.producer.NSQDispatcher;
import fr.vsct.dt.nsq.exceptions.NSQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Subscribes for events from eventbus and dispatch them to NSQDispatcher.
 */
public class ToNSQSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToNSQSubscriber.class);

    private final NSQDispatcher nsqDispatcher;

    ToNSQSubscriber(NSQDispatcher nsqDispatcher) {
        this.nsqDispatcher = nsqDispatcher;
    }

    public void handle(CommitRequestedEvent commitRequestedEvent) throws NSQException, TimeoutException, JsonProcessingException, UnsupportedEncodingException {
        EntryPoint configuration = commitRequestedEvent.getConfiguration();
        Map<String, String> context = configuration.getContext();
        String application = context.get("application");
        String platform = context.get("platform");
        /* TODO test application and platform nullity */
        this.nsqDispatcher.sendCommitRequested(commitRequestedEvent, configuration.getHaproxy(), application, platform, commitRequestedEvent.getBind());
    }

    public void handle(DeleteEntryPointEvent deleteEntryPointEvent) throws NSQException, TimeoutException, JsonProcessingException {
        this.nsqDispatcher.sendDeleteRequested(deleteEntryPointEvent.getCorrelationId(), deleteEntryPointEvent.getHaproxyName(), deleteEntryPointEvent.getApplication(), deleteEntryPointEvent.getPlatform());
    }
}
