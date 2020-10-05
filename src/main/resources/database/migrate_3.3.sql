alter table customized_group add CGR_DISPLAY_NAME longtext;

INSERT INTO fonction (FCT_NAME) VALUES ('FCTN_SMS_ENVOI_CONTACT'), ('FCTN_CONTACT_CREATE'), ('FCTN_CONTACT_SHARE');

create table contact (ID integer not null auto_increment, OWNER varchar(255), LABEL varchar(255), PHONES longtext not null, primary key (ID)) ENGINE=InnoDB;
create index CONTACT_OWNER_LABEL on contact (OWNER, LABEL);

create table contact_share (CONTACT_ID integer not null, CGR_ID integer not null, primary key (CONTACT_ID, CGR_ID)) ENGINE=InnoDB;
alter table contact_share add constraint FK_ln30oerlbxxdcqw04qxe133pc foreign key (CONTACT_ID) references contact (CONTACT_ID);
alter table contact_share add constraint FK_q47jm5n43g9v4n458phed4kbd foreign key (CGR_ID) references customized_group (CGR_ID);

create table to_contact (MSG_ID integer not null, CONTACT_ID integer not null, primary key (MSG_ID, CONTACT_ID)) ENGINE=InnoDB;
alter table to_contact add constraint FK_135nyf7xub8ly6gvjnag93xy3 foreign key (CONTACT_ID) references contact (CONTACT_ID);
alter table to_contact add constraint FK_tlwcg0i5ebyx0aaik2m7fjin3 foreign key (MSG_ID) references message (MSG_ID);
