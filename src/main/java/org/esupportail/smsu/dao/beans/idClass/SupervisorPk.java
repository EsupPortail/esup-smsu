package org.esupportail.smsu.dao.beans.idClass;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.esupportail.smsu.dao.beans.CustomizedGroup;
import org.esupportail.smsu.dao.beans.Person;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class SupervisorPk implements Serializable {
	private CustomizedGroup group;
	private Person person;
}
