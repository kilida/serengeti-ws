/***************************************************************************
 * Copyright (c) 2014-2015 VMware, Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package com.vmware.bdd.plugin.ambari.service.am;

import javax.ws.rs.core.Response;

import com.vmware.bdd.plugin.ambari.api.v1.resource.clusters.RequestsResource;

public class FakeRequestsResource implements RequestsResource {

   @Override
   public Response readRequests() {
      return BuildResponse.buildResponse("clusters/simple_requests.json");
   }

   @Override
   public Response postRequest(String request) {
      return BuildResponse.buildResponse("clusters/simple_request.json");
   }

   @Override
   public Response readRequest(Long RequestId) {
      return BuildResponse.buildResponse("clusters/simple_request.json");
   }

   @Override
   public Response readRequestWithTasks(Long RequestId, String fields) {
      return BuildResponse.buildResponse("clusters/simple_request.json");
   }

}
