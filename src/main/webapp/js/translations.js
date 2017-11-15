(function () {
"use strict";

var app = angular.module('myApp');

app.config(function ($translateProvider) {
  $translateProvider.translations('fr', {
      FCTN_GESTIONS_RESPONSABLES    : "20 Groupes : gestion des superviseurs",
      FCTN_GESTION_GROUPE           : "21 Groupes : gestion des informations",
      FCTN_GESTION_QUOTAS           : "22 Groupes : gestion des quotas",
      FCTN_GESTION_ROLES_AFFECT     : "23 Groupes : gestion des rôles",
      FCTN_GESTION_MODELES          : "30 Modèles : gestion",
      FCTN_GESTION_ROLES_CRUD       : "40 Rôles : gestion",
      FCTN_GESTION_SERVICES_CP      : "50 Thème/partenaires : gestion",
      FCTN_SMS_ENVOI_ADH            : "01 Envoi SMS : à des adhérents",
      FCTN_SMS_ENVOI_GROUPES        : "02 Envoi SMS : à un groupe",
      FCTN_SMS_ENVOI_NUM_TEL        : "03 Envoi SMS : à des numéros de SMS",
      FCTN_SMS_ENVOI_LISTE_NUM_TEL  : "04 Envoi SMS : à des numéros de SMS (en masse)",
      FCTN_SMS_REQ_LDAP_ADH         : "05 Envoi SMS : au résultat d'un filtre LDAP",
      FCTN_SMS_ENVOI_SERVICE_CP     : "06 Envoi SMS : aux adhérents ayant souscrit à un service régit par des conditions particulières",
      FCTN_SMS_AJOUT_MAIL           : "07 Envoi SMS : autoriser les mails (conjointement au SMS)",
      FCTN_SUIVI_ENVOIS_UTIL        : "10 Suivi des envois : envois de l'utilisateur",
      FCTN_SUIVI_ENVOIS_ETABL       : "11 Suivi des envois : tous",

      APPROVALS_CANCEL           : "Refuser",
      APPROVALS_APPROVE          : "Approuver",
      APPROVALS_NO_MESSAGES      : "Tous les messages ont été approuvés",

      MSG_GROUP_RECIPIENT       : "Groupe destinataire",
      MSG_GROUP_SENDER          : "Groupe d'envoi",
      MSG_ACCOUNT               : "Compte d'imputation",
      MSG_SERVICE               : "Thème/Partenaire",
      MSG_CONTENT               : "Contenu",
      MSG_SUPERVISORS		: "Superviseurs",
      MSG_RECIPIENTS		: "Destinataires",

      MSG_STATE			: "État",
      MSG_MAIL_STATE		: "État du mail",
      MSG_INFO_NBR_RECIPIENTS	: "Nb destinataires",
      MSG_INFO_NBR_SENT		: "Nb reçus",
      MSG_INFO_NBR_BLACKLIST	: "Nb invalides",

      MSG_STATE_SENT		: "Envoyé",
      MSG_STATE_CANCEL		: "Annulé",
      MSG_STATE_IN_PROGRESS     : "En cours",
      MSG_STATE_WAITING_FOR_APPROVAL : "En approbation",
      MSG_STATE_WAITING_FOR_SENDING : "En attente d'envoi",
      MSG_STATE_WS_QUOTA_ERROR  : "Erreur de quota",
      MSG_STATE_WS_ERROR	: "Erreur web service",

      MSG_MAIL_STATE_SENT	: "Envoyé",
      MSG_MAIL_STATE_WAITING	: "En attente",
      MSG_MAIL_STATE_ERROR	: "Erreur",

      SMS_ENVOI_LISTE_NUM_TEL   : "Liste de numéros de téléphone",
      SMS_ENVOI_GROUPES         : "Groupe d'utilisateurs",
      SMS_ENVOI_ADH             : "Utilisateur adhérent",
      SMS_ENVOI_NUM_TEL         : "Numéros de téléphone",
      SMS_REQ_LDAP_ADH          : "Recherche des destinataires adhérents via une requête LDAP",

      ADHESION_ERROR_INVALIDPHONENUMBER: "Numéro de téléphone non valide.",
      GROUPE_ACCOUNT_ERROR_MESSAGE     : "Veuillez saisir un compte d''imputation",
      GROUPE_LABEL_ERROR_MESSAGE       : "Veuillez saisir un libellé pour le groupe",
      GROUPE_LABEL_EXIST_ERROR_MESSAGE : "Un groupe porte déjà ce libellé. Veuillez saisir un autre.",
      GROUPE_ROLE_ERROR_MESSAGE        : "Veuillez choisir un rôle",
      SEND_SEARCH_DATES_ERROR          : "Attention : Date début > Date fin !!!",
      SENDSMS_MESSAGE_EMPTYMESSAGE     : "Le message est vide.",
      SENDSMS_MESSAGE_LDAPREQUESTERROR : "Erreur de requête LDAP.",
      SENDSMS_MESSAGE_MESSAGETOOLONG   : "Le message est trop long.",
      SENDSMS_MESSAGE_RECIPIENTSMANDATORY: "Le message ne possède pas de destinataires.",
      SERVICE_CLIENT_NOTDEFINED        : "Utilisateur non authentifié",
      SERVICE_KEY_ERROR                : "La clé du service est déjà utilisée.",
      SERVICE_NAME_ERROR               : "Le nom de service est déjà utilisé.",
      TEMPLATE_BODY_ERROR              : "Le corps ne peut dépasser 160 caractères.",
      TEMPLATE_LABEL_ERROR             : "Le libellé proposé est déjà utilisé.",     
  });
  $translateProvider.preferredLanguage('fr');
});

})();