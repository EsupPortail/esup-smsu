package org.esupportail.smsu.domain.beans.fonction;

/**
 * 
 * @author hbcp0056
 *
 */

/* must be in sync with populate_tables_esup-smsu.sql */
public enum FonctionName {
	// Send SMS
	FCTN_SMS_ENVOI_ADH,	
	// Send SMS Groups
	FCTN_SMS_ENVOI_GROUPES,	
	// Send SMS tel. number
	FCTN_SMS_ENVOI_NUM_TEL,		
	// Send SMS Ldap
	FCTN_SMS_REQ_LDAP_ADH,
	// Send SMS add mail
	FCTN_SMS_AJOUT_MAIL,
	// Manage supervisors
	FCTN_GESTIONS_RESPONSABLES,
	// Manage roles
	FCTN_GESTION_ROLES_CRUD,
	// Roles affect
	FCTN_GESTION_ROLES_AFFECT,
	// Templates manage
	FCTN_GESTION_MODELES,
	// Manage services 
	FCTN_GESTION_SERVICES_CP,
	// Manage Quotas
	FCTN_GESTION_QUOTAS,
	// Manage Track SMS
	FCTN_SUIVI_ENVOIS_UTIL,
	// Manage Track SMS establish
	FCTN_SUIVI_ENVOIS_ETABL,
	// Manage groups
	FCTN_GESTION_GROUPE,

	// Approve SMS. Obsoleted by dynamic "isSupervisor" based on table "supervisor"
	// FCTN_APPROBATION_ENVOI,

	// Send SMS list tel. number
	FCTN_SMS_ENVOI_LISTE_NUM_TEL,
	
}
