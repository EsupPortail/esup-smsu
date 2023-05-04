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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represent role access.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
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
	 * Constructor for required fields.
	 */
	public Role(final Integer id, final String name) {
		this.setId(id);
		this.setName(name);
	}

	public Role(final Role role) {
		this(role.getId(), role.getName().trim());
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
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Role#" + hashCode() + "[id=[" + id + "], name=[" + name + "], functions=[" + fonctions + "]]";
	}
}