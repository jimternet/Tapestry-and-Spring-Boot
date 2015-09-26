package com.noofinc;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.tapestry5.TapestryFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.ErrorPageFilter;
import org.springframework.boot.context.web.ServletContextApplicationContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class TapestrySpringBootServletInitializer extends SpringBootServletInitializer {
	
	@Bean
	public ServletContextInitializer servletContextInitializer() {
		System.out.println("##################################################");

		return (ServletContext servletContext) -> {

			servletContext.setInitParameter("tapestry.app-package", "com.noofinc");

			servletContext.addFilter("app", "org.apache.tapestry5.TapestryFilter").addMappingForUrlPatterns(
					EnumSet.of(DispatcherType.REQUEST), false, "/*");
		};
	}
    
    
 
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    
//	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
		System.out.println("##################################################");

		if (rootAppContext != null) {
			
			servletContext.addFilter("app", "org.apache.tapestry5.TapestryFilter");

			servletContext.setInitParameter("tapestry.app-package", "com.noofinc");
			FilterRegistration.Dynamic tapestryFilter = servletContext.addFilter("app", new TapestryFilter());
			tapestryFilter.addMappingForUrlPatterns(null, true, "/*");
			tapestryFilter.setAsyncSupported(true);
			
			servletContext.addListener(new ContextLoaderListener(rootAppContext) {
				@Override
				public void contextInitialized(ServletContextEvent event) {
					// no-op because the application context is already initialized
				}
			});
		}
		else {
			this.logger.debug("No ContextLoaderListener registered, as "
					+ "createRootApplicationContext() did not "
					+ "return an application context");
		}
	}
	
	
    
//	@Override
	public void onStartup2(ServletContext servletContext) throws ServletException {

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
	
	
	
	protected WebApplicationContext createRootApplicationContext(
			ServletContext servletContext) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder();
		builder.main(getClass());
		ApplicationContext parent = getExistingRootWebApplicationContext(servletContext);
		if (parent != null) {
			this.logger.info("Root context already created (using as parent).");
			servletContext.setAttribute(
					WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
			builder.initializers(new ParentContextApplicationContextInitializer(parent));
		}
		builder.initializers(new ServletContextApplicationContextInitializer(
				servletContext));
		builder.contextClass(AnnotationConfigEmbeddedWebApplicationContext.class);
		builder = configure(builder);
		SpringApplication application = builder.build();
		if (application.getSources().isEmpty()
				&& AnnotationUtils.findAnnotation(getClass(), Configuration.class) != null) {
			application.getSources().add(getClass());
		}
		Assert.state(application.getSources().size() > 0,
				"No SpringApplication sources have been defined. Either override the "
						+ "configure method or add an @Configuration annotation");
		// Ensure error pages are registered
		application.getSources().add(ErrorPageFilter.class);
		return run(application);
	}
	
	private ApplicationContext getExistingRootWebApplicationContext(
			ServletContext servletContext) {
		Object context = servletContext
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (context instanceof ApplicationContext) {
			return (ApplicationContext) context;
		}
		return null;
	}
	
	
}