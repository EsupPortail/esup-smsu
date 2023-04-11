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
 * The class that represents basic groups.
 */
@Entity
@Table(name = "basic_group")
public class BasicGroup  implements Serializable {

	/**
	 * Hibernate reference for the basic group.
	 */
	public static final String REF = "basicGroup";

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
	private static final long serialVersionUID = -2436913232913567894L;

	/**
	 * basic group identifier.
	 */
	@Id
	@Column(name = "BGR_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * basic group label.
	 */
	@Column(name = "BGR_LABEL", nullable = false, length = 255, unique = true)
	@NotNull
	private String label;

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
		final Integer id,
		final String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="BGR_ID"
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
	 * Return the value associated with the column: BGR_LABEL.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: BGR_LABEL.
	 * @param label the BGR_LABEL value
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * @see Object#equals(Object)
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
		return "BasicGroup#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}

}