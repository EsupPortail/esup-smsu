package org.esupportail.smsu.services.smtp;

import java.util.LinkedList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.smtp.SmtpService;

/**
 * 
 * @author PRQD8824
 */
public class SmtpServiceUtils {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Esup commons smtp manager.
	 */
	private SmtpService smtpService;

	
	public void sendMessage(final List<String> toAdresses,
							final List<String> copyAdresses,
							final String subject,
							final String textBody) {
		
		
		// create to adresses
		final List<InternetAddress> toInternetAdressesList = createInternetAdresses(toAdresses);
		
		// create cc adresses
		final List<InternetAddress> ccInternetAdressesList = createInternetAdresses(copyAdresses);

		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Sending email to : \n");
			sb.append(" - to : ");
			for (InternetAddress  internetAddress : toInternetAdressesList) {
				sb.append(" - ").append(internetAddress.getAddress());
			}
			sb.append("\n - cc : ");
			for (InternetAddress  internetAddress : ccInternetAdressesList) {
				sb.append(" - ").append(internetAddress.getAddress());
			}
			sb.append("\n - Subject : ").append(subject);
			sb.append("\n - body : ").append(textBody);
			logger.debug(textBody);
			
		}
		
		final InternetAddress[] toInternetAdressesAsArray = toInternetAdressesList.toArray(new InternetAddress[toInternetAdressesList.size()]);  
		
		InternetAddress[] ccInternetAdressesAsArray;
		if (ccInternetAdressesList.size() > 0) {
			ccInternetAdressesAsArray = ccInternetAdressesList.toArray(new InternetAddress[ccInternetAdressesList.size()]);	
		} else {
			ccInternetAdressesAsArray = null;
		}
		
		smtpService.sendtocc(toInternetAdressesAsArray, ccInternetAdressesAsArray, null, subject, null, textBody, null);
			
	}
	
	
	/**
	 * Create internet adresses from string.
	 * @param adresses
	 * @return
	 */
	private List<InternetAddress> createInternetAdresses(final List<String> adresses) {
		// create to adresses
		final List<InternetAddress> internetAdressesList = new LinkedList<InternetAddress>();

		if(adresses != null) {
			// create to adresses list
			for(String adress : adresses) {
				try {
					final InternetAddress internetAddress = new InternetAddress(adress);
					internetAdressesList.add(internetAddress);
				} catch (AddressException e) {
					logger.warn(adress + " is not a valid email adress, message won't be sent to it");
				}
			}
		}
		return internetAdressesList;
	}

	/**
	 * Check internet adresses from string.
	 * @param adresse
	 * @return
	 */
	public boolean checkInternetAdresses(final String adresse) {
		
		boolean retVal = false;

		if (adresse != null) {
			// check adresse

			try {
				final InternetAddress[] internetAddress = InternetAddress.parse(adresse, true);
				if (internetAddress.length > 0) { 
				internetAddress[0].validate(); 
				retVal = true;
				}
			} catch (AddressException e) {
				logger.warn(adresse + " is not a valid email adress, message won't be sent to it");
			}

		}
		
		return retVal;

	}

	
	/**
	 * Standard setter used by Spring.
	 * @param smtpService
	 */
	public void setSmtpService(final SmtpService smtpService) {
		this.smtpService = smtpService;
	}
	
	
}
