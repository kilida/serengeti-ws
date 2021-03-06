/*
 * Schema for Serengeti.
 */

DROP DATABASE IF EXISTS serengeti;

DROP ROLE IF EXISTS serengeti;

CREATE ROLE serengeti WITH LOGIN PASSWORD 'password';

CREATE DATABASE serengeti WITH OWNER = serengeti ENCODING = 'UTF-8' TEMPLATE template0;

\c serengeti;
\c - serengeti;

create sequence cloud_provider_config_seq;
create table cloud_provider_config (
   id           bigint       not null unique DEFAULT nextval('cloud_provider_config_seq'::regclass),
   attribute    varchar(255) not null,
   cloud_type   varchar(255) not null,
   value        varchar(255),
   primary key (id)
);

create sequence vc_datastore_seq;
create table vc_datastore (
   id           bigint       not null unique DEFAULT nextval('vc_datastore_seq'::regclass),
   name         varchar(255) not null,
   type         varchar(255) not null,
   vc_datastore varchar(255) not null,
   regex        boolean,
   primary key (id)
);

create sequence vc_resource_pool_seq;
create table vc_resource_pool (
   id           bigint       not null unique DEFAULT nextval('vc_resource_pool_seq'::regclass),
   name         varchar(255) not null unique,
   vc_cluster   varchar(255) not null,
   vc_rp        varchar(255) not null,
   primary key (id)
);

create sequence network_seq;
create table network (
   id           bigint       not null unique DEFAULT nextval('network_seq'::regclass),
   name         varchar(255) not null unique,
   port_group   varchar(255) not null,
   alloc_type   varchar(255) not null,
   netmask      varchar(255),
   gateway      varchar(255),
   dns1         varchar(255),
   dns2         varchar(255),
   total        bigint,
   free         bigint,
   dns_type     varchar(255) not null,
   is_generate_hostname boolean not null,
   primary key (id)
);

create sequence ip_block_seq;
create table ip_block (
   id           bigint       not null unique DEFAULT nextval('ip_block_seq'::regclass),
   type         varchar(255) not null,
   network_id   bigint       not null,
   owner_id     bigint       not null,
   begin_ip     bigint       not null,
   end_ip       bigint       not null,
   primary key (id),
   foreign key(network_id) references network(id) ON DELETE CASCADE
);

create sequence appmanager_seq;
create table appmanager (
   id              bigint        not null unique DEFAULT nextval('appmanager_seq'::regclass),
   name            varchar(255)  not null unique,
   description     varchar(1000),
   type            varchar(255)  not null,
   url             varchar(255)  not null,
   username        varchar(255)  not null,
   password        varchar(2048) not null,
   ssl_certificate varchar(2048),
   primary key (id)
);

create sequence cluster_seq;
create table cluster (
   id                  bigint       not null unique DEFAULT nextval('cluster_seq'::regclass),
   name                varchar(255) not null unique,
   password            varchar(2048),
   distro              varchar(255),
   distro_vendor       varchar(255),
   distro_version      varchar(255),
   topology            varchar(255) not null,
   status              varchar(255) not null,
   template_id         varchar(255),
   vc_datastore_names  text,
   vc_rp_names         text,
   start_after_deploy  boolean,
   automation_enable   boolean,
   vhm_min_num         integer,
   vhm_max_num         integer DEFAULT -1,
   vhm_target_num      integer,
   ioshare_type        varchar(16),
   latest_task_id      bigint,
   vhm_master_moid     varchar(255),
   vhm_jobtracker_port varchar(255),
   network_config      text,
   configuration       text,
   version             varchar(255),
   last_status         varchar(255),
   appmanager          varchar(255),
   advanced_properties text,
   infrastructure_config text,
   primary key (id)
);

create sequence node_group_seq;
create table node_group (
   id                     bigint       not null unique DEFAULT nextval('node_group_seq'::regclass),
   name                   varchar(255) not null,
   roles                  text,
   node_type              integer,
   cpu                    integer,
   memory                 integer,
   latencysensitivity     varchar(255),
   reservedcpu_ratio      real,
   reservedmem_ratio      real,
   swap_ratio             real,
   defined_instance_num   integer not null,
   ha_flag                varchar(10),
   storage_type           varchar(255),
   storage_size           integer,
   disk_number            integer,
   is_share_datastore     boolean,
   is_provisioning        boolean,
   vc_datastore_names     text,
   sd_datastore_names     text,
   dd_datastore_names     text,
   vc_rp_names            text,
   group_racks            text,
   vm_folder_path         text,
   configuration          text,
   instance_per_host      integer,
   cluster_id             bigint,
   primary key (id),
   foreign key(cluster_id) references cluster(id) ON DELETE CASCADE
);
create index ng_cluster_id on node_group(cluster_id);

create sequence node_group_association_seq;
create table node_group_association (
   id                 bigint       not null unique DEFAULT nextval('node_group_association_seq'::regclass),
   referenced_group   varchar(255) not null,
   association_type   varchar(255),
   node_group_id      bigint,
   primary key (id),
   foreign key(node_group_id) references node_group(id) ON DELETE CASCADE
);

create sequence node_seq;
create table node (
   id           bigint       not null unique DEFAULT nextval('node_seq'::regclass),
   vm_name      varchar(255) not null unique,
   moid         varchar(255) unique,
   rack         varchar(255),
   host_name    varchar(255),
   status       varchar(255),
   action       varchar(255),
   action_failed              boolean,
   error_message  text,
   power_status_changed       boolean,
   vc_datastores text,
   volumes       text,
   guest_host_name  varchar(255),
   node_group_id bigint,
   vc_rp_id     bigint,
   cpu_number             integer,
   memory                 bigint,
   version                varchar(255),
   primary key (id),
   foreign key(node_group_id) references node_group(id) ON DELETE CASCADE,
   foreign key(vc_rp_id) references vc_resource_pool(id) ON DELETE CASCADE
);
create index node_ng_id on node(node_group_id);

create sequence disk_seq;
create table disk (
   id             bigint       not null unique DEFAULT nextval('disk_seq'::regclass),
   name           varchar(255),
   size           integer,
   alloc_type     varchar(255),
   disk_type      varchar(255),
   external_addr  varchar(255),
   hardware_uuid  varchar(255),
   ds_moid        varchar(255),
   ds_name        varchar(255),
   vmdk_path      varchar(255),
   node_id        bigint,
   primary key (id),
   foreign key(node_id) references node(id) ON DELETE CASCADE
);
create index disk_node_id on disk(node_id);

create sequence nic_seq;
create table nic (
   id             bigint       not null unique DEFAULT nextval('nic_seq'::regclass),
   ipv4_address   varchar(255),
   ipv6_address   varchar(255),
   mac_address    varchar(255),
   fqdn           varchar(255),
   connected      boolean,
   net_traffic_definition  text,
   node_id        bigint,
   network_id     bigint,
   primary key (id),
   foreign key(node_id) references node(id) ON DELETE CASCADE,
   foreign key(network_id) references network(id)
);
create index nic_node_id on nic(node_id);

create sequence rack_seq;
create table rack (
   id           bigint       not null unique DEFAULT nextval('rack_seq'::regclass),
   name         varchar(255) not null,
   primary key (id)
);

create sequence physical_host_seq;
create table physical_host (
   id           bigint       not null unique DEFAULT nextval('physical_host_seq'::regclass),
   name         varchar(255) not null,
   rack_id      bigint,
   primary key (id),
   foreign key(rack_id) references rack(id) ON DELETE CASCADE
);

create sequence server_info_seq;
create table server_info (
  id           bigint       not null unique DEFAULT nextval('server_info_seq'::regclass),
  resource_initialized boolean not null DEFAULT false,
  version      varchar(255),
  instance_id  varchar(255),
  deploy_time  timestamp(0) without time zone
);

create sequence node_template_seq;
create table node_template (
   id          bigint       not null unique DEFAULT nextval('node_template_seq'::regclass),
   name        varchar(255),
   moid        varchar(255),
   last_modified timestamp without time zone,
   tag         varchar(255),
   os_family   varchar(255),
   os_version  varchar(255),
   primary key (id)
);
create index on node_template (name);
create index on node_template (moid);

create table usermgmtserver (
  name        VARCHAR(50),
  type        VARCHAR(30) not null ,
  baseGroupDn VARCHAR(200) not null ,
  baseUserDn  VARCHAR(200) not null ,
  primaryUrl  VARCHAR(200) not null ,
  userName    VARCHAR(200) not null ,
  password VARCHAR(512) not null ,
  primary key (name)
);

create table mgmtvmcfg (
  name varchar (200),
  value varchar (1000),
  primary key (name)
);

insert into mgmtvmcfg values ('vmconfig.mgmtvm.cum.mode','LOCAL');
