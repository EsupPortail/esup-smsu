alter table customized_group modify CGR_LABEL varchar(255) NOT NULL;

-- initial create_tables.sql was written by hand and did not use "datetime" for hibernate "timestamp"
-- (on this subject, see https://discourse.hibernate.org/t/why-does-hibernate-orm-uses-datetime-by-default-on-mysql-instead-of-timestamp/422 )
-- when migrating to mariadb 10.5, it reports old "timestamp" in a specific way (cf https://mariadb.com/kb/en/timestamp/#internal-format )
-- we either migrate to new mariadb TIMESTAMP internal format
-- or we migrate to what hibernate really expects, "datetime":
alter table message modify MSG_DATE datetime not null;
alter table pending_member modify MBR_DATE_SUBSCRIPTION datetime not null;
