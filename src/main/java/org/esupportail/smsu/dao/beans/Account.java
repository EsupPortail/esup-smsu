package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The class that represents accounts.
 */
@Entity
@Table(name = "account")
public class Account implements Serializable {

	/**
	 * Hibernate reference for account.
	 */
	public static final String REF = "account";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";
	
	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "label";
	

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 2840889867044286899L;

	/**
	 * Account identifier.
	 */
	@Id
	@Column(name = "ACC_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Account label.
	 */
	@Column(name = "ACC_LABEL", nullable = false, length = 32, unique = true)
	@NotNull
	private String label;

	/**
	 * Bean constructor.
	 */
	public Account() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Account(
		final Integer id,
		final String label) {

		this.setId(id);
		this.setLabel(label);
	}

	public Account(String label) {
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="ACC_ID"
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
	 * Return the value associated with the column: ACC_LABEL.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: ACC_LABEL.
	 * @param label the ACC_LABEL value
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Account)) {
			return false;
		} else {
			Account account = (Account) obj;
			if (null == this.getId() || null == account.getId()) {
				return false;
			} else {
				return this.getId().equals(account.getId());
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
		return "Account#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}


}