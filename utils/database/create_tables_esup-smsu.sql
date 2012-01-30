/*==============================================================*/
/* Nom de SGBD :  MySQL 5.0                                     */
/* Date de création :  08/28/2009 15:02:50                      */
/*==============================================================*/


/*==============================================================*/
/* Table : ACCOUNT                                              */
/*==============================================================*/
create table account
(
   ACC_ID               int not null auto_increment,
   ACC_LABEL            varchar(32) not null,
   primary key (ACC_ID)
)
type = InnoDB;

alter table account
   add unique AK_ACCAK (ACC_LABEL);

/*==============================================================*/
/* Table : BASIC_GROUP                                          */
/*==============================================================*/
create table basic_group
(
   BGR_ID               int not null auto_increment,
   BGR_LABEL            varchar(32) not null,
   primary key (BGR_ID)
)
type = InnoDB;

alter table basic_group
   add unique AK_BGRAK (BGR_LABEL);

/*==============================================================*/
/* Table : B_VERS_MANA                                          */
/*==============================================================*/
create table b_vers_mana
(
   ID                   int not null auto_increment,
   VERS                 varchar(255),
   primary key (ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : CUSTOMIZED_GROUP                                     */
/*==============================================================*/
create table customized_group
(
   CGR_ID               int not null auto_increment,
   ROL_ID               int not null,
   ACC_ID               int not null,
   CGR_LABEL            varchar(32) not null,
   CGR_QUOTA_SMS        bigint not null,
   CGR_QUOTA_ORDER      bigint not null,
   CGR_CONSUMED_SMS     bigint not null,
   primary key (CGR_ID)
)
type = InnoDB;

alter table customized_group
   add unique AK_CGRAK (CGR_LABEL);

/*==============================================================*/
/* Table : FONCTION                                             */
/*==============================================================*/
create table fonction
(
   FCT_ID               int not null auto_increment,
   FCT_NAME             varchar(32) not null,
   primary key (FCT_ID)
)
type = InnoDB;

alter table fonction
   add unique AK_FCTAK (FCT_NAME);

/*==============================================================*/
/* Table : MAIL                                                 */
/*==============================================================*/
create table mail
(
   MAIL_ID              int not null auto_increment,
   TPL_ID               int,
   MAIL_CONTENT         varchar(300) not null,
   MAIL_STATE           varchar(16) not null,
   MAIL_SUBJECT         varchar(300),
   primary key (MAIL_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : MAIL_RECIPIENT                                       */
/*==============================================================*/
create table mail_recipient
(
   MRC_ID               int not null auto_increment,
   MRC_ADDRESS          varchar(100) not null,
   MRC_LOGIN            varchar(32),
   primary key (MRC_ID)
)
type = InnoDB;

alter table mail_recipient
   add unique AK_MRCAK (MRC_ADDRESS);

/*==============================================================*/
/* Table : MESSAGE                                              */
/*==============================================================*/
create table message
(
   MSG_ID               int not null auto_increment,
   SVC_ID               int,
   BGR_RECIPIENT_ID     int,
   TPL_ID               int,
   ACC_ID               int not null,
   PER_ID               int not null,
   MAIL_ID              int,
   BGR_SENDER_ID        int not null,
   MSG_DATE             timestamp not null default CURRENT_TIMESTAMP,
   MSG_CONTENT          varchar(160) not null,
   MSG_STATE            varchar(32) not null,
   primary key (MSG_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : PENDING_MEMBER                                       */
/*==============================================================*/
create table pending_member
(
   MBR_LOGIN            varchar(32) not null,
   MBR_VALIDATION_CODE  varchar(8) not null,
   MBR_DATE_SUBSCRIPTION timestamp not null default CURRENT_TIMESTAMP,
   primary key (MBR_LOGIN)
)
type = InnoDB;

/*==============================================================*/
/* Table : PERSON                                               */
/*==============================================================*/
create table person
(
   PER_ID               int not null auto_increment,
   PER_LOGIN            varchar(32) not null,
   primary key (PER_ID)
)
type = InnoDB;

alter table person
   add unique AK_PERAK (PER_LOGIN);

/*==============================================================*/
/* Table : RECIPIENT                                            */
/*==============================================================*/
create table recipient
(
   RCP_ID               int not null auto_increment,
   RCP_PHONE            varchar(255) not null,
   RCP_LOGIN            varchar(32),
   primary key (RCP_ID)
)
type = InnoDB;

alter table recipient
   add unique AK_RCPAK (RCP_PHONE);

/*==============================================================*/
/* Table : ROLE                                                 */
/*==============================================================*/
create table role
(
   ROL_ID               int not null auto_increment,
   ROL_NAME             varchar(32) not null,
   primary key (ROL_ID)
)
type = InnoDB;

alter table role
   add unique AK_ROLAK (ROL_NAME);

/*==============================================================*/
/* Table : ROLE_COMPOSITION                                     */
/*==============================================================*/
create table role_composition
(
   ROL_ID               int not null,
   FCT_ID               int not null,
   primary key (ROL_ID, FCT_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : SERVICE                                              */
/*==============================================================*/
create table service
(
   SVC_ID               int not null auto_increment,
   SVC_NAME             varchar(32) not null,
   SVC_KEY              varchar(16) not null,
   primary key (SVC_ID)
)
type = InnoDB;

alter table service
   add unique AK_SVCNAMEAK (SVC_NAME);

alter table service
   add unique AK_SVCKEYAK (SVC_KEY);

/*==============================================================*/
/* Table : SUPERVISOR                                           */
/*==============================================================*/
create table supervisor
(
   CGR_ID               int not null,
   PER_ID               int not null,
   primary key (CGR_ID, PER_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : SUPERVISOR_SENDER                                    */
/*==============================================================*/
create table supervisor_sender
(
   MSG_ID               int not null,
   PER_ID               int not null,
   primary key (MSG_ID, PER_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/
create table template
(
   TPL_ID               int not null auto_increment,
   TPL_LABEL            varchar(32) not null,
   TPL_HEADING          varchar(50),
   TPL_BODY             varchar(160),
   TPL_SIGNATURE        varchar(50),
   primary key (TPL_ID)
)
type = InnoDB;

alter table template
   add unique AK_TPLAK (TPL_LABEL);

/*==============================================================*/
/* Table : TO_MAIL_RECIPIENT                                    */
/*==============================================================*/
create table to_mail_recipient
(
   MAIL_ID              int not null,
   MRC_ID               int not null,
   primary key (MAIL_ID, MRC_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : TO_RECIPIENT                                         */
/*==============================================================*/
create table to_recipient
(
   MSG_ID               int not null,
   RCP_ID               int not null,
   primary key (MSG_ID, RCP_ID)
)
type = InnoDB;

alter table customized_group add constraint FK_ACCOUNT_GROUP foreign key (ACC_ID)
      references account (ACC_ID) on delete restrict on update restrict;

alter table customized_group add constraint FK_ROLE_GROUP foreign key (ROL_ID)
      references role (ROL_ID) on delete restrict on update restrict;

alter table mail add constraint FK_TEMPLATE_MAIL foreign key (TPL_ID)
      references template (TPL_ID) on delete restrict on update restrict;

alter table message add constraint FK_ACCOUNT_SENDER foreign key (ACC_ID)
      references account (ACC_ID) on delete restrict on update restrict;

alter table message add constraint FK_GROUP_RECEPIENT foreign key (BGR_RECIPIENT_ID)
      references basic_group (BGR_ID) on delete restrict on update restrict;

alter table message add constraint FK_GROUP_SENDER foreign key (BGR_SENDER_ID)
      references basic_group (BGR_ID) on delete restrict on update restrict;

alter table message add constraint FK_MAIL_PLUS2 foreign key (MAIL_ID)
      references mail (MAIL_ID) on delete restrict on update restrict;

alter table message add constraint FK_PERSON_SENDER foreign key (PER_ID)
      references person (PER_ID) on delete restrict on update restrict;

alter table message add constraint FK_SERVICE_SENDER foreign key (SVC_ID)
      references service (SVC_ID) on delete restrict on update restrict;

alter table message add constraint FK_TEMPLATE_MESSAGE foreign key (TPL_ID)
      references template (TPL_ID) on delete restrict on update restrict;

alter table role_composition add constraint FK_ROLE_COMPOSITION foreign key (ROL_ID)
      references role (ROL_ID) on delete restrict on update restrict;

alter table role_composition add constraint FK_ROLE_COMPOSITION2 foreign key (FCT_ID)
      references fonction (FCT_ID) on delete restrict on update restrict;

alter table supervisor add constraint FK_SUPERVISOR foreign key (CGR_ID)
      references customized_group (CGR_ID) on delete restrict on update restrict;

alter table supervisor add constraint FK_SUPERVISOR2 foreign key (PER_ID)
      references person (PER_ID) on delete restrict on update restrict;

alter table supervisor_sender add constraint FK_SUPERVISOR_SENDER foreign key (MSG_ID)
      references message (MSG_ID) on delete restrict on update restrict;

alter table supervisor_sender add constraint FK_SUPERVISOR_SENDER2 foreign key (PER_ID)
      references person (PER_ID) on delete restrict on update restrict;

alter table to_mail_recipient add constraint FK_TO_MAIL_RECIPIENT foreign key (MAIL_ID)
      references mail (MAIL_ID) on delete restrict on update restrict;

alter table to_mail_recipient add constraint FK_TO_MAIL_RECIPIENT2 foreign key (MRC_ID)
      references mail_recipient (MRC_ID) on delete restrict on update restrict;

alter table to_recipient add constraint FK_TO_RECIPIENT foreign key (MSG_ID)
      references message (MSG_ID) on delete restrict on update restrict;

alter table to_recipient add constraint FK_TO_RECIPIENT2 foreign key (RCP_ID)
      references recipient (RCP_ID) on delete restrict on update restrict;

commit;
