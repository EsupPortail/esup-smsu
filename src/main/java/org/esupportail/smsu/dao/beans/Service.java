package org.esupportail.smsu.dao.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class that represent services.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "service")
public class Service implements Serializable {

	/**
	 * Hibernate reference for service.
	 */
	public static final String REF = "service";

	/**
	 * Hibernate property for the key.
	 */
	public static final String PROP_KEY = "key";

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
	private static final long serialVersionUID = 2450143530342732074L;

	/**
	 * Service identifier.
	 */
	@Id
	@Column(name = "SVC_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Service name.
	 */
	@Column(name = "SVC_NAME", nullable = false, length = 32, unique = true)
	@NotNull
	private String name;

	/**
	 * Service key.
	 */
	@Column(name = "SVC_KEY", nullable = false, length = 16, unique = true)
	@NotNull
	private String key;

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Service)) {
			return false;
		} else {
			Service service = (Service) obj;
			if (null == this.getId() || null == service.getId()) {
				return false;
			} else {
				return this.getId().equals(service.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Service#" + hashCode() + "[id=[" + id + "], name=[" + name + "], key=[" + key + "]]";
	}
}