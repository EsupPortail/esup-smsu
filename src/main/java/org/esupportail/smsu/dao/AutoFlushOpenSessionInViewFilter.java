package org.esupportail.smsu.dao;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

/* standard OpenSessionInViewFilter + FlushMode.AUTO */
/* thanks to https://stackoverflow.com/questions/25620303/how-can-i-globally-set-flushmode-for-hibernate-4-3-5-final-with-spring-4-0-6 */
public class AutoFlushOpenSessionInViewFilter extends OpenSessionInViewFilter {

  protected Session openSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
    try {
      Session session = sessionFactory.openSession();
      session.setFlushMode(FlushMode.AUTO); // This line changes the default behavior
      return session;
    } catch (HibernateException ex) {
      throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
    }
  }
}