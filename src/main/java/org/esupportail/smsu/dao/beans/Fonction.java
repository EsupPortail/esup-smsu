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
 * The class that represent fonctions (right access).
 */
@Entity
@Table(name = "fonction")
public class Fonction implements Serializable {

	/**
	 * Hibernate reference for the function.
	 */
	public static final String REF = "fonction";

	/**
	 * Hibernate property for the name.
	 */
	public static final String PROP_NAME = "name";
	
	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Id of the function.
	 */
	@Id
	@Column(name = "FCT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * function name.
	 */
	@Column(name = "FCT_NAME", nullable = false, length = 64, unique = true)
	@NotNull
	private String name;

	/**
	 * Bean constructor.
	 */
	public Fonction() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Fonction(
		final Integer id,
		final String name) {
		this.setId(id);
		this.setName(name);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="FCT_ID"
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
	 * Return the value associated with the column: FCT_NAME.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: FCT_NAME.
	 * @param name the FCT_NAME value
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Fonction)) {
			return false;
		} else {
			Fonction fonction = (Fonction) obj;
			if (null == this.getId() || null == fonction.getId()) {
				return false;
			} else {
				return this.getId().equals(fonction.getId());
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
		return "Function#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "]]";
	}


}