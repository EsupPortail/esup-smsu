-- remove unique on RCP_PHONE
alter table recipient modify RCP_PHONE varchar(255) not null;
drop index RCP_PHONE on recipient;
-- add new index on both phone&login
create index RCP_PHONE_LOGIN on recipient (RCP_PHONE, RCP_LOGIN);
