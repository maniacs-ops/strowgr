/*
 * Copyright (C) 2016 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vsct.dt.strowgr.admin.gui.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsct.dt.strowgr.admin.core.configuration.IncomingEntryPointBackendServer;

import java.util.Map;

public class IncomingEntryPointBackendServerJsonRepresentation extends IncomingEntryPointBackendServer {

    @JsonCreator
    public IncomingEntryPointBackendServerJsonRepresentation(@JsonProperty("id") String id,
                                                             @JsonProperty("hostname") String hostname ,//TODO remove hostname attribute
                                                             @JsonProperty("ip") String ip,
                                                             @JsonProperty("port") String port,
                                                             @JsonProperty("context") Map<String, String> context) {
        super(id, ip, port, context);
    }

}
