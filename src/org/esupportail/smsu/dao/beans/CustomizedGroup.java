package org.esupportail.smsu.dao.beans;

import java.io.Serializable;



/**
 * The class that represents customized groups.
 */
public class CustomizedGroup implements Serializable {

	/**
	 * Hibernate reference for customized group.
	 */
	public static final String REF = "CustomizedGroup";

	/**
	 * Hibernate property for the account.
	 */
	public static final String PROP_ACCOUNT = "Account";

	/**
	 * Hibernate property for the number of recipient by sms.
	 */
	public static final String PROP_QUOTA_ORDER = "QuotaOrder";

	/**
	 * Hibernate property for the quota of sms.
	 */
	public static final String PROP_QUOTA_SMS = "QuotaSms";

	/**
	 * Hibernate property for the number of consumed sms.
	 */
	public static final String PROP_CONSUMED_SMS = "ConsumedSms";

	/**
	 * Hibernate property for the role.
	 */
	public static final String PROP_ROLE = "Role";

	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "Label";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -3409241721229863372L;

	/**
	 * customized group identifier.
	 */
	private java.lang.Integer id;

	/**
	 * label of the group.
	 */
	private java.lang.String label;

	/**
	 * quota of sms.
	 */
	private java.lang.Long quotaSms;

	/**
	 * number of recipient by sms.
	 */
	private java.lang.Long quotaOrder;
	
	/**
	 * number of consumed sms.
	 */
	private java.lang.Long consumedSms;

	/**
	 * account associated to the group.
	 */
	private Account account;
	
	/**
	 * role.
	 */
	private Role role;

	/**
	 * collection of supervisors.
	 */
	private java.util.Set<Person> supervisors;

	/**
	 * Bean constructor.
	 */
	public CustomizedGroup() {
		super();
		quotaSms = Long.parseLong("0");
		quotaOrder = Long.parseLong("0");
		consumedSms = Long.parseLong("0");
	}


	/**
	 * Constructor for required fields.
	 */
	public CustomizedGroup(
		final java.lang.Integer id,
		final Account account,
		final Role role,
		final java.lang.String label,
		final java.lang.Long quotaSms,
		final java.lang.Long quotaOrder,
		final java.lang.Long consumedSms) {

		this.setId(id);
		this.setAccount(account);
		this.setRole(role);
		this.setLabel(label);
		this.setQuotaSms(quotaSms);
		this.setQuotaOrder(quotaOrder);
		this.setConsumedSms(consumedSms);
	}





	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="CGR_ID"
     */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.Integer id) {
		this.id = id;
	}




	/**
	 * Return the value associated with the column: CGR_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: CGR_LABEL.
	 * @param label the CGR_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: CGR_QUOTA_SMS.
	 */
	public java.lang.Long getQuotaSms() {
		return quotaSms;
	}

	/**
	 * Set the value related to the column: CGR_QUOTA_SMS.
	 * @param quotaSms the CGR_QUOTA_SMS value
	 */
	public void setQuotaSms(final java.lang.Long quotaSms) {
		this.quotaSms = quotaSms;
	}

	/**
	 * Check whether the account is allowed to send nbSms
	 */
	public boolean checkQuotaSms(int nbToSend) {
		long nbAvailable = getQuotaSms() - getConsumedSms(); 		
		return nbAvailable >= nbToSend;
	}


	/**
	 * Return the value associated with the column: CGR_QUOTA_ORDER.
	 */
	public java.lang.Long getQuotaOrder() {
		return quotaOrder;
	}

	/**
	 * Set the value related to the column: CGR_QUOTA_ORDER.
	 * @param quotaOrder the CGR_QUOTA_ORDER value
	 */
	public void setQuotaOrder(final java.lang.Long quotaOrder) {
		this.quotaOrder = quotaOrder;
	}



	/**
	 * Return the value associated with the column: CGR_CONSUMED_SMS.
	 */
	public java.lang.Long getConsumedSms() {
		return consumedSms;
	}

	/**
	 * Set the value related to the column: CGR_CONSUMED_SMS.
	 * @param consumedSms the CGR_CONSUMED_SMS value
	 */
	public void setConsumedSms(final java.lang.Long consumedSms) {
		this.consumedSms = consumedSms;
	}



	/**
	 * Return the value associated with the column: ACC_ID.
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Set the value related to the column: ACC_ID.
	 * @param account the ACC_ID value
	 */
	public void setAccount(final Account account) {
		this.account = account;
	}



	/**
	 * Return the value associated with the column: ROL_ID.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Set the value related to the column: ROL_ID.
	 * @param role the ROL_ID value
	 */
	public void setRole(final Role role) {
		this.role = role;
	}



	/**
	 * Return the value associated with the column: Supervisors.
	 */
	public java.util.Set<Person> getSupervisors() {
		return supervisors;
	}

	/**
	 * Set the value related to the column: Supervisors.
	 * @param supervisors the Supervisors value
	 */
	public void setSupervisors(final java.util.Set<Person> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * add person to the collection of supervisors.
	 * @param person
	 */
	public void addToSupervisors(final Person person) {
		if (null == getSupervisors()) {
			setSupervisors(new java.util.TreeSet<Person>());
		}
		getSupervisors().add(person);
	}




	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof CustomizedGroup)) {
			return false;
		} else {
			CustomizedGroup customizedGroup = (CustomizedGroup) obj;
			if (null == this.getId() || null == customizedGroup.getId()) {
				return false;
			} else {
				return this.getId().equals(customizedGroup.getId());
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
		return "CustomizedGroup#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}


}