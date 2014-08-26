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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.vmware.bdd.plugin.ambari.api.v1.resource.clusters.ServicesResource;

public class FakeServicesResource implements ServicesResource {

   private String clusterName;

   public FakeServicesResource(String clusterName) {
      this.clusterName = clusterName;
   }

   @Override
   @PUT
   @Path("/")
   @Consumes("application/xml")
   public Response stopAllServices(
         @PathParam("clusterName") String clusterName,
         @QueryParam("params/run_smoke_test") String runSmockTest,
         String request) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   @PUT
   @Path("/")
   @Consumes("application/xml")
   public Response startAllServices(
         @PathParam("clusterName") String clusterName,
         @QueryParam("params/run_smoke_test") String runSmockTest,
         String request) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   @GET
   @Path("/")
   public Response readServicesWithFilter(@QueryParam("fields") String fields) {
      return BuildResponse.buildResponse("clusters/simple_cluster_servicesWithAlert.json");
   }

   @Override
   @DELETE
   @Path("/{serviceName}")
   public Response deleteService(@PathParam("serviceName") String serviceName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   @GET
   @Path("/{serviceName}")
   public Response readService(@PathParam("serviceName") String serviceName) {
      // TODO Auto-generated method stub
      return null;
   }

}
