package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.esupportail.smsu.dao.beans.idClass.ToRecipientPk;


/**
 * This is an object that contains data related to the to_recipient table.
 * @hibernate.class
 *  table="to_recipient"
 */
@Entity
@Table(name = ToRecipient.TABLE_NAME)
@IdClass(ToRecipientPk.class)
public class ToRecipient  implements Serializable {

	/**
	 * Hibernate reference for the association ToRecipient.
	 */
	public static final String REF = "toRecipient";

	public static final String TABLE_NAME = "to_recipient";
	public static final String MSG_COLUMN = "MSG_ID";
	public static final String RECIPIENT_COLUMN = "RCP_ID";
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -4284664919584984842L;

	/**
	 * recipient identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = RECIPIENT_COLUMN)
	private Recipient rcp;

	/**
	 * message identifier.
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = MSG_COLUMN)
	private Message msg;


	/**
	 * Bean constructor.
	 */
	public ToRecipient() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public ToRecipient(final Recipient rcp, final Message msg) {
		this.setRcp(rcp);
		this.setMsg(msg);
	}



	/**
     * @hibernate.property
     *  column=RCP_ID
	 * not-null=true
	 */
	public Recipient getRcp() {
		return this.rcp;
	}

	/**
	 * Set the value related to the column: RCP_ID.
	 * @param rcp the RCP_ID value
	 */
	public void setRcp(final Recipient rcp) {
		this.rcp = rcp;
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof ToRecipient)) {
			return false;
		} else {
			ToRecipient toRecipient = (ToRecipient) obj;
			if (null != this.getRcp() && null != toRecipient.getRcp()) {
				if (!this.getRcp().equals(toRecipient.getRcp())) {
					return false;
				}
			} else {
				return false;
			}
			if (null != this.getMsg() && null != toRecipient.getMsg()) {
				if (!this.getMsg().equals(toRecipient.getMsg())) {
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
		return "ToRecipient#" + hashCode() + "[recipient=[" + rcp + "], message=[" + msg 
		+ "]]";
	}


}