package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * This is an object that contains data related to the supervisor_sender table.
 *
 * @hibernate.class
 *  table="supervisor_sender"
 */
public class SupervisorSender  implements Serializable {

	/**
	 * Hibernate reference for supervisorSender.
	 */
	public static final String REF = "SupervisorSender";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -4139881053707487325L;

	/**
	 * supervisor identifier.
	 */
	private Person supervisor;

	/**
	 * message identifier.
	 */
	private Message msg;

	/**
	 * Bean constructor.
	 */
	public SupervisorSender() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public SupervisorSender(
		final Person supervisor,
		final Message msg) {

		this.setSupervisor(supervisor);
		this.setMsg(msg);
	}

	/**
     * @hibernate.property
     *  column=PER_ID
	 * not-null=true
	 */
	public Person getSupervisor() {
		return this.supervisor;
	}

	/**
	 * Set the value related to the column: PER_ID.
	 * @param supervisor the PER_ID value
	 */
	public void setSupervisor(final Person supervisor) {
		this.supervisor = supervisor;
	}

	/**
     * @hibernate.property
     *  column=MSG_ID
	 * not-null=true
	 */
	public Message getMsg() {
		return this.msg;
	}

	/**
	 * Set the value related to the column: MSG_ID.
	 * @param msg the MSG_ID value
	 */
	public void setMsg(final Message msg) {
		this.msg = msg;
	}


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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Supervisor sender#" + hashCode() + "[person id=[" + supervisor + "], message id=[" + msg 
		+  "]]";
	}


}