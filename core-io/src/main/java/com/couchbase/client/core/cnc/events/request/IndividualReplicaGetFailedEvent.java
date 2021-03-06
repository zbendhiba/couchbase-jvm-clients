/*
 * Copyright (c) 2018 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.core.cnc.events.request;

import com.couchbase.client.core.cnc.AbstractEvent;
import com.couchbase.client.core.msg.RequestContext;

import java.time.Duration;

/**
 * When a replica get fetches more than one in one shot and an individual item fails it
 * will be ignored, but this event is logged to aid with debugging.
 *
 * @since 2.0.0
 */
public class IndividualReplicaGetFailedEvent extends AbstractEvent {

  public IndividualReplicaGetFailedEvent(final RequestContext context) {
    super(Severity.DEBUG, Category.REQUEST, Duration.ZERO, context);
  }

  @Override
  public String description() {
    return "Individual replica get failed";
  }
}
