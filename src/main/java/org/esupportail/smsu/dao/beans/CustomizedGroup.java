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

import org.apache.commons.lang3.math.NumberUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The class that represents customized groups.
 */
// lombok
@AllArgsConstructor
@Getter
@Setter
// JPA
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
		setQuotaSms(NumberUtils.LONG_ZERO);
		setQuotaOrder(NumberUtils.LONG_ZERO);
		setConsumedSms(NumberUtils.LONG_ZERO);
	}

	/**
	 * Check whether the account is allowed to send nbSms
	 */
	public boolean checkQuotaSms(int nbToSend) {
		long nbAvailable = getQuotaSms() - getConsumedSms();
		return nbAvailable >= nbToSend;
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
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomizedGroup#" + hashCode() + "[id=[" + id + "], label=[" + label + "]]";
	}
}