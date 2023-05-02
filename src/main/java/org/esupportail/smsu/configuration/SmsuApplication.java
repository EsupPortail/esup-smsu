package org.esupportail.smsu.configuration;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.esupportail.smsu.dao.JpaDaoServiceImpl;
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.dao.repositories.MessageRepository;
import org.esupportail.smsu.services.UrlGenerator;
import org.esupportail.smsu.web.AuthAndRoleAndMiscFilter;
import org.esupportail.smsu.web.CsrfPreventionFilterHttpHeader;
import org.esupportail.smsu.web.StartPage;
import org.esupportail.smsu.web.TransactionManagerFilter;
import org.esupportail.smsu.web.controllers.ServicesSmsuController;
import org.esupportail.smsu.web.controllers.UsersController;
import org.esupportail.smsu.web.controllers.exceptionmappers.InvalidParameterExceptionMapper;
import org.esupportail.smsu.web.ws.WsController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ImportResource({ "classpath:/properties/applicationContext.xml" })
@PropertySource("classpath:/properties/config.properties")
@EntityScan(basePackageClasses = Message.class)
@EnableJpaRepositories(basePackageClasses = MessageRepository.class)
@EnableTransactionManagement
@ComponentScans({ //
		@ComponentScan(basePackageClasses = { //
				UrlGenerator.class, //
				ServicesSmsuController.class, //
				InvalidParameterExceptionMapper.class, //
				WsController.class, //
		}), })
//@EnableWebMvc
@Import({ CasConfiguration.class, // enable cas client if ${authentication} is "cas"
//		WebConfig.class, //
		JpaDaoServiceImpl.class, TransactionManagerFilter.class, StartPage.class, AuthAndRoleAndMiscFilter.class })
public class SmsuApplication {
	public static final String REST_ROOT_URI = "/rest";
	public static final String REST_LOGIN_URI = REST_ROOT_URI + "/login";
	public static final String REST_URIS = REST_ROOT_URI + "/*";
	public static final String WS_ROOT_URI = "/ws";
	public static final String WS_URIS = WS_ROOT_URI + "/*";

	public static void main(String[] args) {
		SpringApplication.run(SmsuApplication.class, args);
	}
	
	@Bean
	CorsFilter corsFilter(@Value("#{'${portal.urls}'.split(',')}") List<String> allowedOrigins) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(allowedOrigins);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		config.setAllowedHeaders(List.of("X-CSRF-TOKEN", "X-Impersonate-User", "Content-Type", "Accept", "Origin"));
		config.setMaxAge(600L); // 600 seconds = 10 minutes

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
	
	// TODO : peut-être utiliser @WebFilter(urlPatterns = *path*) ? 

	@Bean
	FilterRegistrationBean<CsrfPreventionFilterHttpHeader> csrfPrevention() {
		FilterRegistrationBean<CsrfPreventionFilterHttpHeader> csrfPrevention = new FilterRegistrationBean<>(new CsrfPreventionFilterHttpHeader());
		csrfPrevention.addUrlPatterns(REST_URIS);
		return csrfPrevention;
	}

	@Bean
	FilterRegistrationBean<TransactionManagerFilter> configureOneTransactionPerRequest(TransactionManagerFilter oneTransactionPerRequest) {
		FilterRegistrationBean<TransactionManagerFilter> oneTransactionPerRequestFilterRegistrationBean = new FilterRegistrationBean<>(oneTransactionPerRequest);
		oneTransactionPerRequestFilterRegistrationBean.addUrlPatterns(REST_URIS);
		oneTransactionPerRequestFilterRegistrationBean.addUrlPatterns(WS_URIS);
		return oneTransactionPerRequestFilterRegistrationBean;
	}

	@Bean
	FilterRegistrationBean<AuthAndRoleAndMiscFilter> configureAuthAndRoleAndMiscFilter(AuthAndRoleAndMiscFilter authAndRoleAndMiscFilter) {
		FilterRegistrationBean<AuthAndRoleAndMiscFilter> authAndRoleAndMiscFilterRegistrationBean = new FilterRegistrationBean<>(authAndRoleAndMiscFilter);
		authAndRoleAndMiscFilterRegistrationBean.addUrlPatterns(REST_URIS);
		return authAndRoleAndMiscFilterRegistrationBean;
	}
}
