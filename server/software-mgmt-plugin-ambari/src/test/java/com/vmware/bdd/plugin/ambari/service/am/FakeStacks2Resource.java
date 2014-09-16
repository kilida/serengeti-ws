/***************************************************************************
 * Copyright (c) 2014 VMware, Inc. All Rights Reserved.
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

import com.vmware.bdd.plugin.ambari.api.v1.resource.stacks.Stacks2Resource;
import com.vmware.bdd.plugin.ambari.api.v1.resource.stacks.VersionsResource;

public class FakeStacks2Resource implements Stacks2Resource {

   @Override
   public Response readStacks() {
      return BuildResponse.buildResponse("stacks/simple_stacks.json");
   }

   @Override
   public Response readStack(String stackName) {
      return BuildResponse.buildResponse("stacks/HDP_stack.json");
   }

   @Override
   public VersionsResource getStackVersionsResource(String stackName) {
      return new FakeVersionsResource();
   }

}
