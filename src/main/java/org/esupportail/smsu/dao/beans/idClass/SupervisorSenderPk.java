package org.esupportail.smsu.dao.beans.idClass;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.beans.Person;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class SupervisorSenderPk implements Serializable {
	private Person supervisor;
	private Message msg;
}
