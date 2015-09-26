package com.noofinc;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.tapestry5.TapestryFilter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/* 
 * <context-param> <param-name>tapestry.app-package</param-name>
 * <param-value>com.noofinc</param-value> </context-param>

 * http://www.leveluplunch.com/blog/2014/08/18/migrating-to-servlet-3-context
 * -parameter-spring/
 */

//@Configuration
public class TapestryConfguration implements WebApplicationInitializer {

//	@Bean
	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);

		registration.addInitParameter("tapestry.app-package", "com.noofinc");
		registration.addUrlMappings("/*");

		return registration;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		System.out.println("GreenhouseWebAppInitializer.onStartup()");

		WebApplicationContext context = getContext();

		servletContext.addListener(new ContextLoaderListener(context));
		servletContext.addFilter("app", "org.apache.tapestry5.TapestryFilter");

		servletContext.setInitParameter("tapestry.app-package", "com.noofinc");

		FilterRegistration.Dynamic tapestryFilter = servletContext.addFilter("app", new TapestryFilter());
		tapestryFilter.addMappingForUrlPatterns(null, true, "/*");
		tapestryFilter.setAsyncSupported(true);

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("servletDispatcher", new DispatcherServlet(
				context));
		dispatcher.setAsyncSupported(true);

		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/*");
	}

	private AnnotationConfigWebApplicationContext getContext() {

		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.scan("com.noofinc");
		return context;
	}
}
