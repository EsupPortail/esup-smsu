package org.esupportail.smsu.services.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;
import org.esupportail.smsu.business.context.ApplicationContextUtils;
import org.esupportail.smsu.business.purge.PeriodicPurge;
import org.esupportail.smsu.business.purge.PurgePendingMember;
import org.esupportail.smsu.dao.DaoService;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.exceptions.ldap.LdapWriteException;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.scheduler.SchedulerUtils;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;

/**
 * Servlet used only during test.
 * @author PRQD8824
 *
 */
public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -518464944485395827L;

	  /**
     * logger
     */
	private final Logger logger = new LoggerImpl(getClass());
	

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest req, 
    					 final HttpServletResponse resp) throws ServletException,
        										   			  	IOException {
        try {
			execute(req, resp);
		} catch (Throwable t) {
			logger.error(t);
		}
    }
    
    private void execute(final HttpServletRequest req, 
    					 final HttpServletResponse resp) {
    	
    	
    	final String testId = req.getParameter("testId");
    	
    	if ("ldap".equalsIgnoreCase(testId)) {
    		testLdap();
    	} else if ("smtp".equalsIgnoreCase(testId)) {
    		testSmtp();
    	} else if ("scheduler".equalsIgnoreCase(testId)) {
    		testScheduler();
    	} else  if ("purgePendingMember".equalsIgnoreCase(testId)) {
    		testPurgePendingMember();
    	} else  if ("purgeMessage".equalsIgnoreCase(testId)) {
    		testPurgeMessage();
    	} else  if ("purgeMail".equalsIgnoreCase(testId)) {
    		testPurgeMail();
    	} else  if ("purgeOrphan".equalsIgnoreCase(testId)) {
    		testPurgeOrphan();
    	} else  if ("periodicPurge".equalsIgnoreCase(testId)) {
    		testPeriodicPurge();
    	}
    }
    
   
    
    private void testLdap() {
    	
    	LdapUtils ldapUtils = (LdapUtils) BeanUtils.getBean("ldapUtils");
    	try {
//			ldapUtils.getLdapUserByUserId("aaron");
//    		String email = ldapUtils.getUserDisplayNameByUserUid("aaron");
//			String pager = ldapUtils.getUserPagerByUid("aaron");
//			ldapUtils.setUserPagerByUid("aaron", "1320021");
//			pager = ldapUtils.getUserPagerByUid("aaron");
//			List<String> tou = ldapUtils.getUserTermsOfUseByUid("aaron");
//			tou.add("blup");
//			ldapUtils.setUserTermsOfUse("aaron", tou);
//			tou = ldapUtils.getUserTermsOfUseByUid("aaron");
//			ldapUtils.clearUserTermsOfUse("aaron");
//			email = ldapUtils.getUserEmailAdressByUid("aaron");
    		
//    		LdapGroup ldapGroup = ldapUtils.getLdapGroupByGroupId("mati01320105");
//    		List<LdapUser>  list = ldapUtils.getLdapUsersByGroupId("mati01320105");
//    		ldapUtils.getRootGroup();
    		
    		ldapUtils.isGeneralConditionValidateByUid("aaron");
    		ldapUtils.addGeneralConditionByUid("aaron");
    		ldapUtils.addGeneralConditionByUid("aaron");
    		ldapUtils.isGeneralConditionValidateByUid("aaron");
    		ldapUtils.removeGeneralConditionByUid("aaron");
    		ldapUtils.isGeneralConditionValidateByUid("aaron");
			
		} catch (LdapUserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LdapWriteException e) {
			e.printStackTrace();
		}
    }
    
    private void testSmtp() {
    	SmtpServiceUtils smtpServiceUtils = (SmtpServiceUtils) BeanUtils.getBean("smtpServiceUtils");
    	
    	final String add1 = "karine.chevalier@orange-ftgroup.com";
    	final String add11 = "mathieu1.janvier@orange-ftgroup.com";
    	@SuppressWarnings("unused")
		final String add2 = "christophe.pinel@orange-ftgroup.com";
    	@SuppressWarnings("unused")
		final String add21 = "rlaoues1.ext@orange-ftgroup.com";
    	final String add22 = "truc truc truc";
    	final List<String> toList = new LinkedList<String>();
    	toList.add(add1);
    	toList.add(add11);
    	final List<String> ccList = new LinkedList<String>();
    	ccList.add(add22);
    	//ccList.add(add21);
    	//ccList.add(add22);
    	smtpServiceUtils.sendMessage(ccList, null, "un sujet de test", "Un message de test");
    }
    
    private void testScheduler() {
    	SchedulerUtils schedulerUtils = (SchedulerUtils) BeanUtils.getBean("schedulerUtils");
    	schedulerUtils.launchSuperviseSmsSending();
    }
    
    private void testPurgePendingMember() {
    	ApplicationContextUtils.initApplicationContext();
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		PurgePendingMember purgePendingMember = (PurgePendingMember) BeanUtils.getBean("purgePendingMember");
        	purgePendingMember.purgePendingMember();
        	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    		logger.error(t);
    	} finally {
    		DatabaseUtils.close();
    	}
    }
    
    private void testPurgeMessage() {
    	ApplicationContextUtils.initApplicationContext();
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DaoService daoService = (DaoService) BeanUtils.getBean("daoService");
    		daoService.deleteMessageOlderThan(new Date(System.currentTimeMillis()));
        	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    		logger.error(t);
    	} finally {
    		DatabaseUtils.close();
    	}
    }
    
    private void testPurgeMail() {
    	ApplicationContextUtils.initApplicationContext();
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DaoService daoService = (DaoService) BeanUtils.getBean("daoService");
    		daoService.deleteOrphanMail();
        	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    		logger.error(t);
    	} finally {
    		DatabaseUtils.close();
    	}
    }
    
    private void testPurgeOrphan() {
    	logger.debug("Debut testPurgeOrphan");
    	ApplicationContextUtils.initApplicationContext();
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DaoService daoService = (DaoService) BeanUtils.getBean("daoService");
    		daoService.deleteOrphanRecipient();
        	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    		logger.error(t);
    	} finally {
    		DatabaseUtils.close();
    	}
    	logger.debug("fin testPurgeOrphan");
    }
    
    private void testPeriodicPurge() {
    	ApplicationContextUtils.initApplicationContext();
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		PeriodicPurge periodicPurge = (PeriodicPurge) BeanUtils.getBean("periodicPurge");
    		periodicPurge.purge();
        	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    		logger.error(t);
    	} finally {
    		DatabaseUtils.close();
    	}
    }
}
