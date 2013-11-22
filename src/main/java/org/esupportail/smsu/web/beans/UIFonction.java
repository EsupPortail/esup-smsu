package org.esupportail.smsu.web.beans;

import java.io.Serializable;


/**
 * The class that represent fonctions (right access).
 */
public class UIFonction implements Serializable {

	/**
	 * Hibernate reference for the function.
	 */
	public static final String REF = "Fonction";

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
	private static final long serialVersionUID = 1L;

	/**
	 * Id of the function.
	 */
	private java.lang.String id;

	/**
	 * function name.
	 */
	private java.lang.String name;

	
	/**
	 * Bean constructor.
	 */
	public UIFonction() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public UIFonction(
		final java.lang.String id,
		final java.lang.String name) {
		this.setId(id);
		this.setName(name);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="FCT_ID"
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
	 * Return the value associated with the column: FCT_NAME.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: FCT_NAME.
	 * @param name the FCT_NAME value
	 */
	public void setName(final java.lang.String name) {
		this.name = name;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UIFonction)) {
			return false;
		} else {
			UIFonction fonction = (UIFonction) obj;
			if (null == this.getId() || null == fonction.getId()) {
				return false;
			} else {
				return this.getId().equals(fonction.getId());
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
		return "Function#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "]]";
	}


}