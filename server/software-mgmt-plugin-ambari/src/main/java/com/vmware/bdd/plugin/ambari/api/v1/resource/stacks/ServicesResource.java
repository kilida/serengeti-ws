/***************************************************************************
 * Copyright (c) 2015 VMware, Inc. All Rights Reserved.
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
package com.vmware.bdd.plugin.ambari.api.v1.resource.stacks;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.vmware.bdd.plugin.ambari.api.Parameters;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN })
public interface ServicesResource {

   @GET
   @Path("/")
   public Response readServices();

   @GET
   @Path("/")
   public Response readServicesWithFilter(@QueryParam("fields") String fields);

   @GET
   @Path("/{stackServiceName}")
   public Response readService(@PathParam(Parameters.STACK_SERVICE_NAME) String stackServiceName);

   @GET
   @Path("/{stackServiceName}")
   public Response readServiceWithFilter(
         @PathParam(Parameters.STACK_SERVICE_NAME) String stackServiceName,
         @QueryParam("fields") String fields);

   @Path("/{stackServiceName}/components")
   public ComponentsResource getComponentsResource(@PathParam(Parameters.STACK_SERVICE_NAME) String stackServiceName);

   @GET
   @Path("/{stackServiceName}/configurations/content")
   public Response readServiceConfigurationWithFilter(@PathParam(Parameters.STACK_SERVICE_NAME) String stackServiceName);

}
