/***************************************************************************
 * Copyright (c) 2012-2015 VMware, Inc. All Rights Reserved.
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
package com.vmware.bdd.service.utils;

import java.lang.reflect.Method;

import mockit.Mock;
import mockit.MockClass;

import com.vmware.aurora.vc.vcservice.VcContext;
import com.vmware.aurora.vc.vcservice.VcSession;

@MockClass(realClass = VcContext.class)
public class MockVcContext {
   @Mock
   public static <T> T inVcSessionDo(VcSession<T> session) {
      try {
         Method body = VcSession.class.getDeclaredMethod("body");
         body.setAccessible(true);
         return (T)body.invoke(session);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   @Mock
   public static synchronized void initVcContext() {}

   @Mock
   public static boolean isServiceConnected() {
      return true;
   }
}
