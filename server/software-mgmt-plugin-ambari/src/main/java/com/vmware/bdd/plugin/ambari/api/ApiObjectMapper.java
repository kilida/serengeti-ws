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
package com.vmware.bdd.plugin.ambari.api;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class ApiObjectMapper extends ObjectMapper {

   private static final long serialVersionUID = -2922528263257124521L;

   public ApiObjectMapper() {
      // Print the JSON with indentation (ie. pretty print)
      configure(SerializationFeature.INDENT_OUTPUT, true);

      // Allow JAX-B annotations.
      setAnnotationIntrospector(
            new AnnotationIntrospector.Pair(
                getSerializationConfig().getAnnotationIntrospector(),
                new JaxbAnnotationIntrospector()));

      // Make Jackson respect @XmlElementWrapper.
      enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);

      // Print all dates in ISO8601 format
      setDateFormat(makeISODateFormat());
   }

   public static DateFormat makeISODateFormat() {
      DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
      iso8601.setCalendar(cal);
      return iso8601;
   }

   public static DateFormat makeDateFormat(String format) {
      DateFormat dateFormat = new SimpleDateFormat(format);
      Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
      dateFormat.setCalendar(cal);
      return dateFormat;
   }
}
