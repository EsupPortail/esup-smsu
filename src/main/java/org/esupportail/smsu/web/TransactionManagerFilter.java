package org.esupportail.smsu.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.esupportail.smsu.services.scheduler.SchedulerUtils;
import org.esupportail.smsu.services.scheduler.job.SuperviseSmsSending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("oneTransactionPerRequest")
public class TransactionManagerFilter implements Filter {

	@Autowired
	TransactionManagerFilterService transactionManagerFilterService;

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		try {
			transactionManagerFilterService.processRequest(request, response, filterChain);
		} finally {
			if (request.getAttribute(SuperviseSmsSending.class.getName()) != null) {
				transactionManagerFilterService.triggerQuartzTasks();
			}
		}
	}

	@Service
	public static class TransactionManagerFilterService {
		@Autowired
		private SchedulerUtils schedulerUtils;

		@Transactional
		public void processRequest(final ServletRequest request, final ServletResponse response,
				final FilterChain filterChain) throws IOException, ServletException {
			filterChain.doFilter(request, response);
		}

		@Transactional
		public void triggerQuartzTasks() {
			schedulerUtils.launchSuperviseSmsSending();
		}
	}
}