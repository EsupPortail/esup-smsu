package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Date;


/**
 * The class that represent users.
 */
public class PendingMember  implements Serializable {
	/**
	 * Hibernate reference for pending member.
	 */
	public static final String REF = "PendingMember";

	/**
	 * Hibernate property for the validation code.
	 */
	public static final String PROP_VALIDATION_CODE = "ValidationCode";

	/**
	 * Hibernate property for the subscription date.
	 */
	public static final String PROP_DATE_SUBSCRIPTION = "DateSubscription";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8373156029880260203L;
	


	/**
	 * Id of the pending member (corresponds to its login).
	 */
	private java.lang.String id;

	
	/**
	 * code to validate the subscription.
	 */
	private java.lang.String validationCode;
	
	/**
	 * subscription date.
	 */
	private java.util.Date dateSubscription;

	/**
	 * Bean constructor.
	 */
	public PendingMember() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public PendingMember(
		final java.lang.String id,
		final java.lang.String validationCode) {

		this.setId(id);
		this.setValidationCode(validationCode);
		this.setDateSubscription(new Date());
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="assigned"
     *  column="MBR_LOGIN"
     */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.String id) {
		this.id = id;
	}

	/**
	 * Return the value associated with the column: MBR_VALIDATION_CODE.
	 */
	public java.lang.String getValidationCode() {
		return validationCode;
	}

	/**
	 * Set the value related to the column: MBR_VALIDATION_CODE.
	 * @param validationCode the MBR_VALIDATION_CODE value
	 */
	public void setValidationCode(final java.lang.String validationCode) {
		this.validationCode = validationCode;
	}

	/**
	 * Return the value associated with the column: MBR_DATE_SUBSCRIPTION.
	 */
	public java.util.Date getDateSubscription() {
		return dateSubscription;
	}

	/**
	 * @param dateSubscription
	 */
	public void setDateSubscription(final java.util.Date dateSubscription) {
		this.dateSubscription = dateSubscription;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof PendingMember)) {
			return false;
		} else {
			PendingMember pendingMember = (PendingMember) obj;
			if (null == this.getId() || null == pendingMember.getId()) {
				return false;
			} else {
				return this.getId().equals(pendingMember.getId());
			}
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
		return "PendingMember#" + hashCode() + "[id=[" + id + "], validationCode=[" + validationCode 
		+ "], dateSubscription=[" + dateSubscription + "]]";
	}

}