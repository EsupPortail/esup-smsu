/**
 * ESUP-Portail Commons - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-commons
 */
package org.esupportail.smsu.services.exceptionHandling;

/**
 * An application-specific exception service.
 * 
 * See /properties/exceptionHandling/exceptionHandling-example.xml.
 */
public class CachingEmailExceptionServiceFactoryImpl 
extends org.esupportail.commons.services.exceptionHandling.CachingEmailExceptionServiceFactoryImpl {
	
	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 2009174670782802670L;
	/**
	 * The developer's list for bugs.
	 */
	private static final String DEVEL_EMAIL = "commons-bugs@esup-portail.org";

	/**
	 * Bean constructor.
	 */
	public CachingEmailExceptionServiceFactoryImpl() {
		super();
	}

	/**
	 * @return The developer's list for bugs.
	 */
	@Override
	public String getDevelEmail() {
		return DEVEL_EMAIL;
	}
	
}
