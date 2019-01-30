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
import org.springframework.beans.factory.annotation.Autowired;


public class TransactionManagerFilter implements Filter {

	@Autowired private SessionFactory sessionFactory;

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
        	}
            catch (Exception e) {
            	transaction.rollback();
			}
            HibernateUtils.closeSession(sessionFactory, participate);
        }
    }
}