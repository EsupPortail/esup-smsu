/*==============================================================*/
/* Insert Init Data		                                        */
/*==============================================================*/

/*  Ajout des fonctions */
/*  must be in sync with FonctionName.java */
INSERT INTO fonction VALUES(1,"FCTN_SMS_ENVOI_ADH");
INSERT INTO fonction VALUES(2,"FCTN_SMS_ENVOI_GROUPES");
INSERT INTO fonction VALUES(3,"FCTN_SMS_ENVOI_NUM_TEL");
INSERT INTO fonction VALUES(4,"FCTN_SMS_REQ_LDAP_ADH");
INSERT INTO fonction VALUES(5,"FCTN_SMS_AJOUT_MAIL");
INSERT INTO fonction VALUES(6,"FCTN_GESTIONS_RESPONSABLES");
INSERT INTO fonction VALUES(7,"FCTN_GESTION_ROLES_CRUD");
INSERT INTO fonction VALUES(8,"FCTN_GESTION_ROLES_AFFECT");
INSERT INTO fonction VALUES(9,"FCTN_GESTION_MODELES");
INSERT INTO fonction VALUES(10,"FCTN_GESTION_SERVICES_CP");
INSERT INTO fonction VALUES(11,"FCTN_GESTION_QUOTAS");
INSERT INTO fonction VALUES(12,"FCTN_SUIVI_ENVOIS_UTIL");
INSERT INTO fonction VALUES(13,"FCTN_SUIVI_ENVOIS_ETABL");
INSERT INTO fonction VALUES(14,"FCTN_GESTION_GROUPE");
INSERT INTO fonction VALUES(15,"FCTN_APPROBATION_ENVOI");
INSERT INTO fonction VALUES(16,"FCTN_SMS_ENVOI_PLUSIEURS_NUM_TEL");

/* Ajout le role SUPER_ADMIN */
INSERT INTO role VALUES(1,"SUPER_ADMIN");

/* Ajout toutes les fonctions au SUPER_ADMIN */
INSERT INTO role_composition VALUES(1,1);
INSERT INTO role_composition VALUES(1,2);
INSERT INTO role_composition VALUES(1,3);
INSERT INTO role_composition VALUES(1,4);
INSERT INTO role_composition VALUES(1,5);
INSERT INTO role_composition VALUES(1,6);
INSERT INTO role_composition VALUES(1,7);
INSERT INTO role_composition VALUES(1,8);
INSERT INTO role_composition VALUES(1,9);
INSERT INTO role_composition VALUES(1,10);
INSERT INTO role_composition VALUES(1,11);
INSERT INTO role_composition VALUES(1,12);
INSERT INTO role_composition VALUES(1,13);
INSERT INTO role_composition VALUES(1,14);
INSERT INTO role_composition VALUES(1,15);
INSERT INTO role_composition VALUES(1,16);

/* Ajout du compte par defaut */
INSERT INTO account VALUES (1, "default_account");

/* Ajout du compte par defaut */
INSERT INTO customized_group VALUES (1, 1, 1, "admin", 1, 1, 1);

commit;