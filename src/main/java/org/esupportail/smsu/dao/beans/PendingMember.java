package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represent users.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "pending_member")
public class PendingMember implements Serializable {
	/**
	 * Hibernate reference for pending member.
	 */
	public static final String REF = "pendingMember";

	/**
	 * Hibernate property for the validation code.
	 */
	public static final String PROP_VALIDATION_CODE = "validationCode";

	/**
	 * Hibernate property for the subscription date.
	 */
	public static final String PROP_DATE_SUBSCRIPTION = "dateSubscription";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8373156029880260203L;

	/**
	 * Id of the pending member (corresponds to its login).
	 */
	@Id
	@Column(name = "MBR_LOGIN")
	private String id;

	/**
	 * code to validate the subscription.
	 */
	@Column(name = "MBR_VALIDATION_CODE", nullable = false, length = 8, unique = true)
	@NotNull
	private String validationCode;

	/**
	 * subscription date.
	 */
	@Column(name = "MBR_DATE_SUBSCRIPTION", nullable = false, length = 8, unique = true)
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateSubscription;

	/**
	 * Constructor for required fields.
	 */
	public PendingMember(String id, String validationCode) {
		this(id, validationCode, new Date());
	}

	/**
	 * @see Object#equals(Object)
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
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "PendingMember#" + hashCode() + "[id=[" + id + "], validationCode=[" + validationCode
				+ "], dateSubscription=[" + dateSubscription + "]]";
	}
}