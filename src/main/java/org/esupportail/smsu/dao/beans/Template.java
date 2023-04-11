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
 * The class that represent template of mail or SMS.
 */
@Entity
@Table(name = "template")
public class Template  implements Serializable {

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
	 * Bean constructor.
	 */
	public Template() {
		super();
	}


	/**
	 * Constructor for required fields.
	 */
	public Template(
		final Integer id,
		final String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="TPL_ID"
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
	 * Return the value associated with the column: TPL_LABEL.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: TPL_LABEL.
	 * @param label the TPL_LABEL value
	 */
	public void setLabel(final String label) {
		this.label = label;
	}



	/**
	 * Return the value associated with the column: TPL_HEADING.
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * Set the value related to the column: TPL_HEADING.
	 * @param heading the TPL_HEADING value
	 */
	public void setHeading(final String heading) {
		this.heading = heading;
	}



	/**
	 * Return the value associated with the column: TPL_BODY.
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Set the value related to the column: TPL_BODY.
	 * @param body the TPL_BODY value
	 */
	public void setBody(final String body) {
		this.body = body;
	}



	/**
	 * Return the value associated with the column: TPL_SIGNATURE.
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Set the value related to the column: TPL_SIGNATURE.
	 * @param signature the TPL_SIGNATURE value
	 */
	public void setSignature(final String signature) {
		this.signature = signature;
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
		return "Template#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+ "], heading=[" + heading + "], body=[" + body + "], signature=[" + signature + "]]";
	}

}