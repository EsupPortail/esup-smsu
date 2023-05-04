package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.esupportail.smsu.dao.beans.idClass.SupervisorSenderPk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is an object that contains data related to the supervisor_sender table.
 *
 * @hibernate.class table="supervisor_sender"
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = SupervisorSender.TABLE_NAME)
@IdClass(SupervisorSenderPk.class)
public class SupervisorSender implements Serializable {

	/**
	 * Hibernate reference for supervisorSender.
	 */
	public static final String REF = "supervisorSender";

	static final String TABLE_NAME = "supervisor_sender";
	static final String MSG_COLUMN = "MSG_ID";
	static final String SUPERVISOR_COLUMN = "PER_ID";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -4139881053707487325L;

	/**
	 * supervisor identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = SUPERVISOR_COLUMN)
	private Person supervisor;

	/**
	 * message identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = MSG_COLUMN)
	private Message msg;

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof SupervisorSender)) {
			return false;
		} else {
			SupervisorSender supervisorSender = (SupervisorSender) obj;
			if (null != this.getSupervisor() && null != supervisorSender.getSupervisor()) {
				if (!this.getSupervisor().equals(supervisorSender.getSupervisor())) {
					return false;
				}
			} else {
				return false;
			}
			if (null != this.getMsg() && null != supervisorSender.getMsg()) {
				if (!this.getMsg().equals(supervisorSender.getMsg())) {
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
		return "Supervisor sender#" + hashCode() + "[person id=[" + supervisor + "], message id=[" + msg + "]]";
	}
}