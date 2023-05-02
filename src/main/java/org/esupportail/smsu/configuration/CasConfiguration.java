package org.esupportail.smsu.configuration;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "authentication", havingValue = "cas")
@EnableCasClient
public class CasConfiguration {
	
	@Bean
	FilterRegistrationBean<AuthenticationFilter> configureCasAuthenticationFilter(FilterRegistrationBean<AuthenticationFilter> casAuthenticationFilter) {
		casAuthenticationFilter.addUrlPatterns(SmsuApplication.REST_LOGIN_URI);
		return casAuthenticationFilter;
	}
	
	@Bean
	FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter> configureCasValidationFilter(FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter> casValidationFilter) {
		casValidationFilter.addUrlPatterns(SmsuApplication.REST_LOGIN_URI);
		return casValidationFilter;
	}
	
	@Bean
    FilterRegistrationBean<SingleSignOutFilter> configureCasSingleSignOutFilter(FilterRegistrationBean<SingleSignOutFilter> casSingleSignOutFilter){
    	casSingleSignOutFilter.addUrlPatterns(SmsuApplication.REST_LOGIN_URI);
    	return casSingleSignOutFilter;
    }
	
	@Bean
	FilterRegistrationBean<HttpServletRequestWrapperFilter> configureCasHttpServletRequestWrapperFilter(FilterRegistrationBean<HttpServletRequestWrapperFilter> casHttpServletRequestWrapperFilter) {
		casHttpServletRequestWrapperFilter.addUrlPatterns(SmsuApplication.REST_URIS);
		return casHttpServletRequestWrapperFilter;
	}
}
