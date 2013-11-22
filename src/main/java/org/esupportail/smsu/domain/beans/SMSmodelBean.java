package org.esupportail.smsu.domain.beans;

import java.io.Serializable;

/**
 * @author xphp8691
 *
 */
public class SMSmodelBean implements Serializable {

	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 4976403205088388554L;
	
	/**
	 * model Id.
	 */
	private String modelId;
	
	/**
	 * prefix.
	 */
	private String prefix;
	
	/**
	 * body.
	 */
	private String body;
	
	/**
	 * suffix.
	 */
	private String suffix;

	/////////////////////
	// Constructors
	/////////////////////
	/**
	 * Bean constructor.
	 */
	public SMSmodelBean() {
		super();
		this.body = "titi";
		this.modelId = "mod3";
		this.prefix = "atat";
		this.suffix = "tutu";
		// TODO Auto-generated constructor stub
	}

	public SMSmodelBean(final String body, final String modelID, 
			final String prefix, final String suffix) {
		super();
		this.body = body;
		this.modelId = modelID;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	
	///////////////////////
	// Getters and Setters
	///////////////////////
	public String getModelID() {
		return modelId;
	}

	public void setModelID(final String modelID) {
		this.modelId = modelID;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public void changeModel() {
		this.body = "montexte";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} 
		if (obj == null) {
			return false;
		} 
		if (getClass() != obj.getClass()) {
			return false;
		}	
		SMSmodelBean other = (SMSmodelBean) obj;
		if (body == null) {
			if (other.body != null) {
				return false;
			}	
		} else if (!body.equals(other.body)) {
			return false;
		}
		if (modelId == null) {
			if (other.modelId != null) {
				return false;
			}	
		} else if (!modelId.equals(other.modelId)) {
			return false;
		}
		if (prefix == null) {
			if (other.prefix != null) {
				return false;
			}				
		} else if (!prefix.equals(other.prefix)) {
			return false;
		}
		if (suffix == null) {
			if (other.suffix != null) {
				return false;
			}
		} else if (!suffix.equals(other.suffix)) {
			return false;
		}
		return true;
	}

}
