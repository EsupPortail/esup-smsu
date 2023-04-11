package org.esupportail.smsu.dao.beans.idClass;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.esupportail.smsu.dao.beans.Mail;
import org.esupportail.smsu.dao.beans.MailRecipient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class ToMailRecipientPk implements Serializable {
	private MailRecipient mailRecipient;
	private Mail mail;
}
