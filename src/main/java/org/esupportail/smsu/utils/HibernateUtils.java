package org.esupportail.smsu.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/* inspired from OpenSessionInViewFilter / esup-commons's HibernateThreadConnectionData */
public class HibernateUtils {

    /* ugly helper function */
    public static SessionFactory getSessionFactory(ApplicationContext applicationContext) {
	return ((HibernateDaoSupport) applicationContext.getBean("daoService")).getSessionFactory();
    }

    /* return true if we "participate" to an existing session */
    public static boolean openSession(SessionFactory sessionFactory) {
	boolean participate = TransactionSynchronizationManager.hasResource(sessionFactory);
	if (!participate) {
	    Session session = SessionFactoryUtils.getSession(sessionFactory, true);
	    session.setFlushMode(org.hibernate.FlushMode.ALWAYS);
	    TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}
	return participate;
    }

    public static void closeSession(SessionFactory sessionFactory, boolean participate) {
	if (!participate) {
	    SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
	    SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
	}
    }
}
