package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.esupportail.smsu.dao.beans.idClass.SupervisorPk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is an object that contains data related to the supervisor table.
 *
 * @hibernate.class table="supervisor"
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "supervisor")
@IdClass(SupervisorPk.class)
public class Supervisor implements Serializable {

	/**
	 * Hibernate reference for account.
	 */
	public static final String REF = "supervisor";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5006528539597549849L;

	/**
	 * customized group.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = "CGR_ID")
	private CustomizedGroup group;

	/**
	 * person that identifies the supervisor.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = "PER_ID")
	private Person person;

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Supervisor)) {
			return false;
		} else {
			Supervisor supervisor = (Supervisor) obj;
			if (null != this.getGroup() && null != supervisor.getGroup()) {
				if (!this.getGroup().equals(supervisor.getGroup())) {
					return false;
				}
			} else {
				return false;
			}
			if (null != this.getPerson() && null != supervisor.getPerson()) {
				if (!this.getPerson().equals(supervisor.getPerson())) {
					return false;
				}
			} else {
				return false;
			}
			return true;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Supervisor#" + hashCode() + "[Customized group=[" + group + "], person=[" + person + "]]";
	}
}