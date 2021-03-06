/******************************************************************************
 *   Copyright (c) 2014-2015 VMware, Inc. All Rights Reserved.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *****************************************************************************/
package com.vmware.bdd.usermgmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vmware.bdd.apitypes.UserMgmtServer;
import com.vmware.bdd.utils.CommonUtil;

/**
 * Created By xiaoliangl on 12/31/14.
 */
@Component
@Scope("singleton")
public class SssdConfigurationGenerator {
   public static final String SSSD_CONF_TEMPLATES = "sssd.conf.templates.";
   private Map<UserMgmtServer.Type, StringBuilder> templateContent = new HashMap<>();

   private Map<UserMgmtServer.Type, Map<String, String>> mapping = new HashMap<>();

   protected StringBuilder getTemplateContent(UserMgmtServer.Type type) {
      return templateContent.get(type);
   }

   protected boolean isTemplateContentEmpty() {
      return templateContent.isEmpty();
   }

   protected void load() {
      if (isTemplateContentEmpty()) {
         Map<UserMgmtServer.Type, StringBuilder> templateMap = new HashMap<>();
         synchronized (templateContent) {
            for (UserMgmtServer.Type type : UserMgmtServer.Type.values()) {
               File templateFile = CommonUtil.getConfigurationFile("usermgmt" + File.separator + SSSD_CONF_TEMPLATES + type, "sssd.conf");
               HashMap<String, String> typeMap = new HashMap<>();

               StringBuilder stringBuilder = new StringBuilder();
               try (BufferedReader templateBufReader = new BufferedReader(new FileReader(templateFile))) {
                  String line = templateBufReader.readLine();

                  boolean flag = false;
                  while (line != null) {
                     if(StringUtils.isNotBlank(line)) {
                        if(!flag) {
                           flag = StringUtils.equals(line, "[domain/LDAP]");
                        }

                        if(flag) {
                           int keyValueSepIndex = line.indexOf('=');
                           if (keyValueSepIndex != -1) {
                              typeMap.put(line.substring(0, keyValueSepIndex).trim(),
                                    StringUtils.substring(line, keyValueSepIndex + 1).trim()
                              );
                           }
                        }
                     }
                     stringBuilder.append(line).append('\n');
                     line = templateBufReader.readLine();
                  }
               } catch (FileNotFoundException fnf) {
                  throw new UserMgmtException("SSSD_CONF_TEMPLATE_NOT_FOUND", fnf, templateFile.getAbsolutePath());
               } catch (IOException ioe) {
                  throw new UserMgmtException("SSSD_CONF_TEMPLATE_READ_ERR", ioe, templateFile.getAbsolutePath());
               }
               templateMap.put(type, stringBuilder);

               mapping.put(type, typeMap);
            }

            templateContent.putAll(templateMap);

         }
      }
   }

   public String getConfigurationContent(UserMgmtServer userMgmtServer, String[] groups) {
      load();

      String configContent = new String(getTemplateContent(userMgmtServer.getType()));

      ArrayList<String[]> replacementList = new ArrayList<>();
      replacementList.add(new String[]{"LDAP_GROUP_SEARCH_BASE_VALUE", userMgmtServer.getBaseGroupDn()});
      replacementList.add(new String[]{"LDAP_USER_SEARCH_BASE_VALUE", userMgmtServer.getBaseUserDn()});
      replacementList.add(new String[]{"LDAP_URI_VALUE", userMgmtServer.getPrimaryUrl()});
      replacementList.add(new String[]{"LDAP_DEFAULT_BIND_DN_VALUE", userMgmtServer.getUserName()});
      replacementList.add(new String[]{"LDAP_DEFAULT_AUTHTOK_VALUE", userMgmtServer.getPassword()});


      StringBuilder stringBuilder = new StringBuilder();
      if (groups.length > 1) {
         stringBuilder.append("(|");
         for (String group : groups) {
            stringBuilder.append("(memberOf=cn=").append(group).append(",").append(userMgmtServer.getBaseGroupDn()).append(')');
         }
         stringBuilder.append(')');
      } else {
         stringBuilder.append("memberOf=cn=").append(groups[0]).append(",").append(userMgmtServer.getBaseGroupDn());
      }

      replacementList.add(new String[]{"LDAP_ACCESS_FILTER_VALUE", stringBuilder.toString()});

      for (String[] replacement : replacementList) {
         configContent = StringUtils.replace(configContent, replacement[0], replacement[1]);
      }

      return configContent;
   }
   public Map<String, String> get(UserMgmtServer.Type type) {
      load();
      return mapping.get(type);
   }
}
