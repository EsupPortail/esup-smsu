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
 * The class that represent template of mail or SMS.
 */
// lombok
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// JPA
@Entity
@Table(name = "template")
public class Template implements Serializable {

	/**
	 * Hibernate reference for template.
	 */
	public static final String REF = "template";

	/**
	 * Hibernate property for the signature.
	 */
	public static final String PROP_SIGNATURE = "signature";

	/**
	 * Hibernate property for the heading.
	 */
	public static final String PROP_HEADING = "heading";

	/**
	 * Hibernate property for the body.
	 */
	public static final String PROP_BODY = "body";

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
	private static final long serialVersionUID = 1044379115170016078L;

	/**
	 * template identifier.
	 */
	@Id
	@Column(name = "TPL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * template label.
	 */
	@Column(name = "TPL_LABEL", nullable = false, length = 32, unique = true)
	@NotNull
	private String label;

	/**
	 * template heading.
	 */
	@Column(name = "TPL_HEADING", length = 50)
	private String heading;

	/**
	 * template body.
	 */
	@Column(name = "TPL_BODY", length = 160)
	private String body;

	/**
	 * template signature.
	 */
	@Column(name = "TPL_SIGNATURE", length = 50)
	private String signature;

	/**
	 * Constructor for required fields.
	 */
	public Template(Integer id, String label) {
		this.setId(id);
		this.setLabel(label);
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Template)) {
			return false;
		} else {
			Template template = (Template) obj;
			if (null == this.getId() || null == template.getId()) {
				return false;
			} else {
				return this.getId().equals(template.getId());
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Template#" + hashCode() + "[id=[" + id + "], label=[" + label + "], heading=[" + heading + "], body=["
				+ body + "], signature=[" + signature + "]]";
	}
}