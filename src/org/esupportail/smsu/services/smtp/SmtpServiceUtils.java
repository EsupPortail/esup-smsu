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


	public void sendOneMessage(final String toAdress,
				   final String subject,
				   final String textBody) {
		sendMessage(singletonList(toAdress), null, subject, textBody);
	}

	public void sendMessage(final List<String> toAdresses,
							final List<String> copyAdresses,
							final String subject,
							final String textBody) {		
		InternetAddress[] to = createInternetAdresses(toAdresses);
		InternetAddress[] cc = createInternetAdresses(copyAdresses);

		// SmtpService expects a non-null "To: " addresses
		if (to == null) to = new InternetAddress[0];
		
		if (logger.isDebugEnabled()) {
			String msg = "Sending email to :" +
				"\n - to : " + join(to, " - ") +
			    (cc == null ? "" : 
			        "\n - cc : " + join(cc, " - ")) +
				"\n - Subject : " + subject +
				"\n - body : " + textBody;
			logger.debug(msg);			
		}
		
		smtpService.sendtocc(to, cc, null, subject, null, textBody, null);
			
	}
	
	
	/**
	 * Create internet adresses from string.
	 * @param adresses
	 * @return
	 */
	private InternetAddress[] createInternetAdresses(final List<String> adresses) {
		if (adresses == null) return null;

		final List<InternetAddress> l = new LinkedList<InternetAddress>();
		for(String adress : adresses) {
			try {
				l.add(new InternetAddress(adress));
			} catch (AddressException e) {
				logger.warn(adress + " is not a valid email adress, message won't be sent to it");
			}
		}

		if (l.size() > 0) {
			return l.toArray(new InternetAddress[0]);	
		} else {
			return null;
		}
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
	

	private <A> LinkedList<A> singletonList(A e) {
		final LinkedList<A> l = new LinkedList<A>();
		l.add(e);
		return l;
	}

	public static String join(Object[] elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}

}
