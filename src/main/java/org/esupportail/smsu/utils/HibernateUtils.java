package org.esupportail.smsu.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/* inspired from OpenSessionInViewFilter / esup-commons's HibernateThreadConnectionData */
public class HibernateUtils {

    /* ugly helper function */
    public static SessionFactory getSessionFactory(ApplicationContext applicationContext) {
	    return (SessionFactory) applicationContext.getBean("sessionFactory");
    }

    /* return true if we "participate" to an existing session */
    public static boolean openSession(SessionFactory sessionFactory) {
	boolean participate = TransactionSynchronizationManager.hasResource(sessionFactory);
	if (!participate) {
	    Session session = sessionFactory.openSession();
	    session.setFlushMode(org.hibernate.FlushMode.ALWAYS);
	    TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}
	return participate;
    }

    public static void closeSession(SessionFactory sessionFactory, boolean participate) {
	if (!participate) {
	    SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
	    sessionHolder.getSession().close();
	}
    }
}
