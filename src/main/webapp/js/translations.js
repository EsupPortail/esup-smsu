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
      APPROVALS_NO_MESSAGES      : "Pas de message à approuver.",

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
  });
  $translateProvider.preferredLanguage('fr');
});

})();