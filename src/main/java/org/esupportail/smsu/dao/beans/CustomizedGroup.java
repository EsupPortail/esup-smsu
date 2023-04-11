package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;



/**
 * The class that represents customized groups.
 */
@Entity
@Table(name = "customized_group")
public class CustomizedGroup implements Serializable {

	/**
	 * Hibernate reference for customized group.
	 */
	public static final String REF = "customizedGroup";

	/**
	 * Hibernate property for the account.
	 */
	public static final String PROP_ACCOUNT = "account";

	/**
	 * Hibernate property for the number of recipient by sms.
	 */
	public static final String PROP_QUOTA_ORDER = "quotaOrder";

	/**
	 * Hibernate property for the quota of sms.
	 */
	public static final String PROP_QUOTA_SMS = "quotaSms";

	/**
	 * Hibernate property for the number of consumed sms.
	 */
	public static final String PROP_CONSUMED_SMS = "consumedSms";

	/**
	 * Hibernate property for the role.
	 */
	public static final String PROP_ROLE = "role";

	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "label";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -3409241721229863372L;

	/**
	 * customized group identifier.
	 */
	@Id
	@Column(name = "CGR_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * label of the group.
	 */
	@Column(name = "CGR_LABEL", nullable = false, length = 255, unique = true)
	@NotNull
	private String label;

	/**
	 * quota of sms.
	 */
	@Column(name = "CGR_QUOTA_SMS", nullable = false, length = 20)
	@NotNull
	private Long quotaSms;

	/**
	 * number of recipient by sms.
	 */
	@Column(name = "CGR_QUOTA_ORDER", nullable = false, length = 20)
	@NotNull
	private Long quotaOrder;
	
	/**
	 * number of consumed sms.
	 */
	@Column(name = "CGR_CONSUMED_SMS", nullable = false, length = 20)
	@NotNull
	private Long consumedSms;

	/**
	 * account associated to the group.
	 */
	@ManyToOne
	@JoinColumn(name = "ACC_ID", nullable = false)
	@NotNull
	private Account account;
	
	/**
	 * role.
	 */
	@ManyToOne
	@JoinColumn(name = "ROL_ID", nullable = false)
	@NotNull
	private Role role;

	/**
	 * collection of supervisors.
	 */
	@ManyToMany
	@JoinTable(name = "supervisor", //
			joinColumns = { @JoinColumn(name = "CGR_ID") }, //
			inverseJoinColumns = { @JoinColumn(name = "PER_ID") })
	private Set<Person> supervisors;

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
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="CGR_ID"
     */
	public Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final Integer id) {
		this.id = id;
	}




	/**
	 * Return the value associated with the column: CGR_LABEL.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: CGR_LABEL.
	 * @param label the CGR_LABEL value
	 */
	public void setLabel(final String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: CGR_QUOTA_SMS.
	 */
	public Long getQuotaSms() {
		return quotaSms;
	}

	/**
	 * Set the value related to the column: CGR_QUOTA_SMS.
	 * @param quotaSms the CGR_QUOTA_SMS value
	 */
	public void setQuotaSms(final Long quotaSms) {
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
	public Long getQuotaOrder() {
		return quotaOrder;
	}

	/**
	 * Set the value related to the column: CGR_QUOTA_ORDER.
	 * @param quotaOrder the CGR_QUOTA_ORDER value
	 */
	public void setQuotaOrder(final Long quotaOrder) {
		this.quotaOrder = quotaOrder;
	}



	/**
	 * Return the value associated with the column: CGR_CONSUMED_SMS.
	 */
	public Long getConsumedSms() {
		return consumedSms;
	}

	/**
	 * Set the value related to the column: CGR_CONSUMED_SMS.
	 * @param consumedSms the CGR_CONSUMED_SMS value
	 */
	public void setConsumedSms(final Long consumedSms) {
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
	public Set<Person> getSupervisors() {
		return supervisors;
	}

	/**
	 * Set the value related to the column: Supervisors.
	 * @param supervisors the Supervisors value
	 */
	public void setSupervisors(final Set<Person> supervisors) {
		this.supervisors = supervisors;
	}


	/**
	 * @see Object#equals(Object)
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
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}


	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomizedGroup#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}


}