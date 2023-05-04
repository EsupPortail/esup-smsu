package org.esupportail.smsu.dao.beans.idClass;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Recipient;

import lombok.AllArgsConstructor;
import lombok.Data;

// lombok
@Data
@AllArgsConstructor
// JPA
@Embeddable
public class ToRecipientPk implements Serializable {
	private Recipient rcp;
	private Message msg;
}
