package org.esupportail.smsu.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.esupportail.smsu.utils.HibernateUtils;
import org.esupportail.smsu.services.scheduler.SchedulerUtils;
import javax.inject.Inject;

public class TransactionManagerFilter implements Filter {

	@Inject private SessionFactory sessionFactory;
	@Inject private SchedulerUtils schedulerUtils;

    public void destroy() {}
    public void init(FilterConfig config) {}

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        boolean participate = HibernateUtils.openSession(sessionFactory);
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        try {
    		filterChain.doFilter(request, response);
        } finally {
        	try {
        		transaction.commit();

        		// now trigger the Quartz tasks (which have their own Hibernate transaction)
        		if (request.getAttribute(org.esupportail.smsu.services.scheduler.job.SuperviseSmsSending.class.getName()) != null) {
        		    schedulerUtils.launchSuperviseSmsSending();
        		}
        	}
            catch (Exception e) {
            	transaction.rollback();
			}
            HibernateUtils.closeSession(sessionFactory, participate);
        }
    }
}