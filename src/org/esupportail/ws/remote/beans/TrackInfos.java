package org.esupportail.ws.remote.beans;

import java.io.Serializable;
import java.util.HashSet;

import org.esupportail.smsu.dao.beans.Account;


/**
 * The class that represents accounts.
 */
public class TrackInfos implements Serializable {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1278768065587081098L;

	/**
	 * number of SMS to send.
	 */
	private java.lang.Integer nbDestTotal;

	/**
	 * number of balcklist SMS.
	 */
	private java.lang.Integer nbDestBlackList;

	/**
	 * number of sent correctly SMS.
	 */
	private java.lang.Integer nbSentSMS;

	/**
	 * number of SMS in progress state.
	 */
	private java.lang.Integer nbProgressSMS;

	/**
	 * number of SMS in error state.
	 */
	private java.lang.Integer nbErrorSMS;

	
	/**
	 * Collection of phone numbers in blacklist.
	 */
	private java.util.Set<String> listNumErreur = new HashSet<String>();

	
	/**
	 * Bean constructor.
	 */
	public TrackInfos() {
		super();
	}

	/**
	 * @param nbDestTotal the nbDestTotal to set
	 */
	public void setNbDestTotal(final java.lang.Integer nbDestTotal) {
		this.nbDestTotal = nbDestTotal;
	}

	/**
	 * @return the nbDestTotal
	 */
	public java.lang.Integer getNbDestTotal() {
		return nbDestTotal;
	}

	/**
	 * @param nbDestBlackList the nbDestBlackList to set
	 */
	public void setNbDestBlackList(final java.lang.Integer nbDestBlackList) {
		this.nbDestBlackList = nbDestBlackList;
	}

	/**
	 * @return the nbDestBlackList
	 */
	public java.lang.Integer getNbDestBlackList() {
		return nbDestBlackList;
	}


	/**
	 * @param nbSentSMS the nbSentSMS to set
	 */
	public void setNbSentSMS(final java.lang.Integer nbSentSMS) {
		this.nbSentSMS = nbSentSMS;
	}

	/**
	 * @return the nbSentSMS
	 */
	public java.lang.Integer getNbSentSMS() {
		return nbSentSMS;
	}


	/**
	 * @param listNumErreur the listNumErreur to set
	 */
	public void setListNumErreur(final java.util.Set<String> listNumErreur) {
		this.listNumErreur = listNumErreur;
	}

	/**
	 * @return the listNumErreur
	 */
	public java.util.Set<String> getListNumErreur() {
		return listNumErreur;
	}
	
	/**
	 * @param nbProgressSMS the nbProgressSMS to set
	 */
	public void setNbProgressSMS(final java.lang.Integer nbProgressSMS) {
		this.nbProgressSMS = nbProgressSMS;
	}

	/**
	 * @return the nbProgressSMS
	 */
	public java.lang.Integer getNbProgressSMS() {
		return nbProgressSMS;
	}

	/**
	 * @param nbErrorSMS the nbErrorSMS to set
	 */
	public void setNbErrorSMS(final java.lang.Integer nbErrorSMS) {
		this.nbErrorSMS = nbErrorSMS;
	}

	/**
	 * @return the nbErrorSMS
	 */
	public java.lang.Integer getNbErrorSMS() {
		return nbErrorSMS;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof TrackInfos)) {
			return false;
		} else {
			TrackInfos trackInfosToThird = (TrackInfos) obj;
			if (null == this.getNbDestTotal() || null == trackInfosToThird.getNbDestTotal()) {
				return false;
			} else {
				return this.getNbDestTotal().equals(trackInfosToThird.getNbDestTotal());
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
		return "TrackInfosToThird#" + hashCode() + "[total=[" + nbDestTotal + "]]";
	}


}