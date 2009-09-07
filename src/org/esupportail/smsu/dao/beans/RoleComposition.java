package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * This is an object that contains data related to the role_composition table.
 *
 * @hibernate.class
 *  table="role_composition"
 */
public class RoleComposition  implements Serializable {


	/**
	 * Hibernate reference for the association roleComposition.
	 */
	public static final String REF = "RoleComposition";
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 8817425497504309071L;

	/**
	 * function identifier.
	 */
	private Fonction fonction;

	/**
	 * role identifier.
	 */
	private Role role;

	/**
	 * Bean constructor.
	 */
	public RoleComposition() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public RoleComposition(
		final Fonction fonction,
		final Role role) {

		this.setFonction(fonction);
		this.setRole(role);
	}





	/**
     * @hibernate.property
     *  column=FCT_ID
	 * not-null=true
	 */
	public Fonction getFonction() {
		return this.fonction;
	}

	/**
	 * Set the value related to the column: FCT_ID.
	 * @param fonction the FCT_ID value
	 */
	public void setFonction(final Fonction fonction) {
		this.fonction = fonction;
	}

	/**
     * @hibernate.property
     *  column=ROL_ID
	 * not-null=true
	 */
	public Role getRole() {
		return this.role;
	}

	/**
	 * Set the value related to the column: ROL_ID.
	 * @param role the ROL_ID value
	 */
	public void setRole(final Role role) {
		this.role = role;
	}





	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof RoleComposition)) {
			return false;
		} else {
			RoleComposition roleComposition = (RoleComposition) obj;
			if (null != this.getFonction() && null != roleComposition.getFonction()) {
				if (!this.getFonction().equals(roleComposition.getFonction())) {
					return false;
				}
			} else {
				return false;
			}
			if (null != this.getRole() && null != roleComposition.getRole()) {
				if (!this.getRole().equals(roleComposition.getRole())) {
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
		return "RoleComposition#" + hashCode() + "[role id=[" + role + "], function=[" + fonction 
		+ "]]";
	}

}