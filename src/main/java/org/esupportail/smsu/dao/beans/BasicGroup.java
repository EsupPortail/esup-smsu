package org.esupportail.smsu.dao.beans;

import java.io.Serializable;


/**
 * The class that represents basic groups.
 */
public class BasicGroup  implements Serializable {

	/**
	 * Hibernate reference for the basic group.
	 */
	public static final String REF = "BasicGroup";

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
	private static final long serialVersionUID = -2436913232913567894L;

	/**
	 * basic group identifier.
	 */
	private java.lang.Integer id;

	/**
	 * basic group label.
	 */
	private java.lang.String label;

	/**
	 * Bean constructor.
	 */
	public BasicGroup() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public BasicGroup(
		final java.lang.Integer id,
		final java.lang.String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="BGR_ID"
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
	 * Return the value associated with the column: BGR_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: BGR_LABEL.
	 * @param label the BGR_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof BasicGroup)) {
			return false;
		} else {
			BasicGroup basicGroup = (BasicGroup) obj;
			if (null == this.getId() || null == basicGroup.getId()) {
				return false;
			} else {
				return this.getId().equals(basicGroup.getId());
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
		return "BasicGroup#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}

}