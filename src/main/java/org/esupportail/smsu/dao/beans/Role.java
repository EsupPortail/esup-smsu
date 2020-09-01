package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represent role access.
 */
public class Role implements Serializable {

	/**
	 * Hibernate reference for role.
	 */
	public static final String REF = "Role";

	/**
	 * Hibernate property for the name.
	 */
	public static final String PROP_NAME = "Name";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -2392564724576331578L;


	/**
	 * role identifier.
	 */
	private java.lang.Integer id;

	/**
	 * role name.
	 */
	private java.lang.String name;

	/**
	 * Set of function access that define the role.
	 */
	private java.util.Set<Fonction> fonctions;

	/**
	 * Bean constructor.
	 */
	public Role() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Role(
		final java.lang.Integer id,
		final java.lang.String name) {
		this.setId(id);
		this.setName(name);
	}

	public Role(final Role role) {
		this(role.getId(), role.getName().trim());
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="sequence"
     *  column="ROL_ID"
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
	 * Return the value associated with the column: ROL_NAME.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: ROL_NAME.
	 * @param name the ROL_NAME value
	 */
	public void setName(final java.lang.String name) {
		this.name = name;
	}

	/**
	 * Return the value associated with the column: Fonctions.
	 */
	public java.util.Set<Fonction> getFonctions() {
		return fonctions;
	}

	/**
	 * Set the value related to the column: Fonctions.
	 * @param fonctions the Fonctions value
	 */
	public void setFonctions(final java.util.Set<Fonction> fonctions) {
		this.fonctions = fonctions;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Role)) {
			return false;
		} else {
			Role role = (Role) obj;
			if (null == this.getId() || null == role.getId()) {
				return false;
			} else {
				return this.getId().equals(role.getId());
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
		return "Role#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "], functions=[" + fonctions + "]]";
	}


}