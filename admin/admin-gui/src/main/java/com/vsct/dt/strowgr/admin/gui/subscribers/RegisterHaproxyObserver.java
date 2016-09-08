package com.vsct.dt.strowgr.admin.gui.subscribers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.lookup.NSQLookup;
import com.vsct.dt.strowgr.admin.core.event.in.CommitSuccessEvent;
import com.vsct.dt.strowgr.admin.nsq.consumer.CommitCompletedConsumer;
import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;

/**
 * ~  Copyright (C) 2016 VSCT
 * ~
 * ~  Licensed under the Apache License, Version 2.0 (the "License");
 * ~  you may not use this file except in compliance with the License.
 * ~  You may obtain a copy of the License at
 * ~
 * ~   http://www.apache.org/licenses/LICENSE-2.0
 * ~
 * ~  Unless required by applicable law or agreed to in writing, software
 * ~  distributed under the License is distributed on an "AS IS" BASIS,
 * ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * ~  See the License for the specific language governing permissions and
 * ~  limitations under the License.
 * ~
 */
public class RegisterHaproxyObserver extends Subscriber<String> {
    private NSQLookup    lookup;
    private ObjectMapper mapper;
    private NSQConfig    config;

    private volatile AsyncEmitter<Observable<CommitSuccessEvent>> commitCompletedObservableEmitter;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(String hapId) {
        commitCompletedObservableEmitter.onNext(new CommitCompletedConsumer(lookup, hapId, mapper, config).observable());
    }

    public Observable<Observable<CommitSuccessEvent>> commitSuccessObservable(String hapId) {
        return Observable.fromEmitter(emitter -> {
            commitCompletedObservableEmitter = emitter;
        }, AsyncEmitter.BackpressureMode.BUFFER);
    }

}
