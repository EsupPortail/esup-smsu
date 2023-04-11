package org.esupportail.smsu.dao.beans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * The class that represent role access.
 */
@Entity
@Table(name = "role")
public class Role implements Serializable {

	/**
	 * Hibernate reference for role.
	 */
	public static final String REF = "role";

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
	private static final long serialVersionUID = -2392564724576331578L;


	/**
	 * role identifier.
	 */
	@Id
	@Column(name = "ROL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * role name.
	 */
	@Column(name = "ROL_NAME", nullable = false, length = 32, unique = true)
	@NotNull
	private String name;

	/**
	 * Set of function access that define the role.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "role_composition", //
			joinColumns = { @JoinColumn(name = "ROL_ID") }, //
			inverseJoinColumns = { @JoinColumn(name = "FCT_ID") })
	private Set<Fonction> fonctions;

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
		final Integer id,
		final String name) {
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
	 * Return the value associated with the column: ROL_NAME.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: ROL_NAME.
	 * @param name the ROL_NAME value
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Return the value associated with the column: Fonctions.
	 */
	public Set<Fonction> getFonctions() {
		return fonctions;
	}

	/**
	 * Set the value related to the column: Fonctions.
	 * @param fonctions the Fonctions value
	 */
	public void setFonctions(final Set<Fonction> fonctions) {
		this.fonctions = fonctions;
	}

	/**
	 * @see Object#equals(Object)
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
		return "Role#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "], functions=[" + fonctions + "]]";
	}


}