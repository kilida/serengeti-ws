/***************************************************************************
 * Copyright (c) 2012 VMware, Inc. All Rights Reserved.
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
package com.vmware.bdd.entity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.vmware.bdd.apitypes.ClusterRead;
import com.vmware.bdd.apitypes.ClusterRead.ClusterStatus;
import com.vmware.bdd.apitypes.NodeGroupRead;
import com.vmware.bdd.apitypes.TopologyType;
import com.vmware.bdd.dal.DAL;
import com.vmware.bdd.utils.AuAssert;
import com.vmware.bdd.utils.ConfigInfo;
import com.vmware.bdd.utils.Configuration;

/**
 * Cluster Entity
 * 
 */
@Entity
@SequenceGenerator(name = "IdSequence", sequenceName = "cluster_seq", allocationSize = 1)
@Table(name = "cluster")
public class ClusterEntity extends EntityBase {

   @Column(name = "name", unique = true, nullable = false)
   private String name;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false)
   private ClusterStatus status;

   @Column(name = "distro")
   private String distro;

   @Enumerated(EnumType.STRING)
   @Column(name = "topology", nullable = false)
   private TopologyType topologyPolicy;

   @Column(name = "start_after_deploy")
   private boolean startAfterDeploy;

   @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private Set<NodeGroupEntity> nodeGroups;

   /*
    * cluster definition field. VCResourcePool inside this array may not be used
    * by this cluster, so we should avoid setting up the ManyToMany mapping.
    * JSON encoded VCResourcePoolEntity name array 
    */
   @Column(name = "vc_rp_names")
   @Type(type = "text")
   private String vcRpNames;

   /*
    * cluster definition field. VCDataStores inside this array may not be used
    * by this cluster, so we should avoid setting up the ManyToMany mapping.
    * JSON encoded VCDataStoreEntity name array 
    */
   @Column(name = "vc_datastore_names")
   @Type(type = "text")
   private String vcDatastoreNames;

   // OneToMany mapping with Network table
   @ManyToOne
   @JoinColumn(name = "network_id")
   private NetworkEntity network;

   @Column(name = "configuration")
   @Type(type = "text")
   private String hadoopConfig;

   ClusterEntity() {

   }

   public ClusterEntity(String name) {
      super();
      this.name = name;
      this.status = ClusterStatus.NA;
   }

   public NetworkEntity getNetwork() {
      return network;
   }

   public void setNetwork(NetworkEntity network) {
      this.network = network;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public TopologyType getTopologyPolicy() {
      return topologyPolicy;
   }

   public void setTopologyPolicy(TopologyType topologyPolicy) {
      this.topologyPolicy = topologyPolicy;
   }

   public ClusterStatus getStatus() {
      return status;
   }

   public void setStatus(ClusterStatus status) {
      this.status = status;
   }

   public String getDistro() {
      return distro;
   }

   public void setDistro(String distro) {
      this.distro = distro;
   }

   public boolean isStartAfterDeploy() {
      return startAfterDeploy;
   }

   public void setStartAfterDeploy(boolean startAfterDeploy) {
      this.startAfterDeploy = startAfterDeploy;
   }

   public Set<NodeGroupEntity> getNodeGroups() {
      return nodeGroups;
   }

   public void setNodeGroups(Set<NodeGroupEntity> nodeGroups) {
      this.nodeGroups = nodeGroups;
   }

   public String getVcRpNames() {
      return this.vcRpNames;
   }

   @SuppressWarnings("unchecked")
   public List<String> getVcRpNameList() {
      return (new Gson()).fromJson(vcRpNames,
            (new ArrayList<String>()).getClass());
   }

   public void setVcRpNameList(List<String> vcRpNameList) {
      this.vcRpNames = (new Gson()).toJson(vcRpNameList);
   }

   public String getVcDatastoreNames() {
      return this.vcDatastoreNames;
   }

   @SuppressWarnings("unchecked")
   public List<String> getVcDatastoreNameList() {
      return (new Gson()).fromJson(vcDatastoreNames,
            (new ArrayList<String>()).getClass());
   }

   public void setVcDatastoreNameList(List<String> vcDatastoreNameList) {
      this.vcDatastoreNames = (new Gson()).toJson(vcDatastoreNameList);
   }

   public int getRealInstanceNum() {
      int instanceNum = 0;
      for (NodeGroupEntity group : nodeGroups) {
         instanceNum += group.getRealInstanceNum();
      }

      return instanceNum;
   }

   public int getDefinedInstanceNum() {
      int instanceNum = 0;
      for (NodeGroupEntity group : nodeGroups) {
         instanceNum += group.getDefineInstanceNum();
      }

      return instanceNum;
   }

   public Set<VcResourcePoolEntity> getUsedRps() {
      Set<VcResourcePoolEntity> rps = new HashSet<VcResourcePoolEntity>();
      for (NodeGroupEntity group : nodeGroups) {
         rps.addAll(group.getUsedVcResourcePools());
      }

      return rps;
   }

   public Set<String> getUsedVcDatastores() {
      HashSet<String> datastores = new HashSet<String>();
      for (NodeGroupEntity group : nodeGroups) {
         datastores.addAll(group.getUsedVcDatastores());
      }

      return datastores;
   }

   public String getHadoopConfig() {
      return hadoopConfig;
   }

   public void setHadoopConfig(String hadoopConfig) {
      this.hadoopConfig = hadoopConfig;
   }

   public ClusterRead toClusterRead() {
      ClusterRead clusterRead = new ClusterRead();
      clusterRead.setInstanceNum(this.getRealInstanceNum());
      clusterRead.setName(this.name);
      clusterRead.setStatus(this.status);
      clusterRead.setDistro(this.distro);
      clusterRead.setTopologyPolicy(this.topologyPolicy);

      List<NodeGroupRead> groupList = new ArrayList<NodeGroupRead>();
      for(NodeGroupEntity group : this.getNodeGroups()) {
         groupList.add(group.toNodeGroupRead());
      }
      clusterRead.setNodeGroups(groupList);
      if (this.getHadoopConfig() != null) {
         Map conf =
            (new Gson()).fromJson(this.getHadoopConfig(), Map.class);
         Map hadoopConf = (Map) conf.get("hadoop");
         if (hadoopConf != null) {
            Map coreSiteConf = (Map) hadoopConf.get("core-site.xml");
            if (coreSiteConf != null) {
               String hdfs = (String) coreSiteConf.get("fs.default.name");
               if (hdfs != null && !hdfs.isEmpty()) {
                  clusterRead.setExternalHDFS(hdfs);
               }
            }
         }
      }

      return clusterRead;
   }

   public String getRootFolder() {
      return ConfigInfo.getSerengetiRootFolder() + "/" + this.name;
   }

   public static ClusterEntity findClusterEntityById(Long clusterId) {
      return DAL.findById(ClusterEntity.class, clusterId);
   }

   public static ClusterEntity findClusterEntityByName(String name) {
      return DAL.findUniqueByCriteria(ClusterEntity.class,
            Restrictions.eq("name", name));
   }

   public static List<ClusterEntity> findClusterEntityByDatastore(String dsName) {
      List<ClusterEntity> clusters = DAL.findByCriteria(ClusterEntity.class,
            Restrictions.like("vcDatastoreNames", dsName, MatchMode.ANYWHERE));
      Iterator<ClusterEntity> i = clusters.iterator();
      while (i.hasNext()) {
         ClusterEntity cluster = i.next();
         if (!cluster.getVcDatastoreNameList().contains(dsName)) {
            i.remove();
         }
      }
      return clusters;
   }

   public static List<ClusterEntity> findClusterEntityByRP(String rpName) {
      List<ClusterEntity> clusters = DAL.findByCriteria(ClusterEntity.class,
            Restrictions.like("vcRpNames", rpName, MatchMode.ANYWHERE));
      Iterator<ClusterEntity> i = clusters.iterator();
      while (i.hasNext()) {
         ClusterEntity cluster = i.next();
         if (!cluster.getVcRpNameList().contains(rpName)) {
            i.remove();
         }
      }
      return clusters;
   }

   public static void updateStatus(final String clusterName, final ClusterStatus status) {
      if (clusterName == null || status == null) 
         return;

      DAL.inTransactionDo(new Saveable<Void>() {
         public Void body() {
            ClusterEntity cluster = ClusterEntity.findClusterEntityByName(clusterName);
            AuAssert.check(cluster != null);
            cluster.setStatus(status);
            return null;
         }
      });
   }
}
