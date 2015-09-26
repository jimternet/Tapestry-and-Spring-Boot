package com.noofinc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
//        SpringApplication.run(new Class[] { Application.class, TapestryConfguration.class }, args);

	}

	/* 
	 * <context-param> <param-name>tapestry.app-package</param-name>
	 * <param-value>com.noofinc</param-value> </context-param>
	 
	 * http://www.leveluplunch.com/blog/2014/08/18/migrating-to-servlet-3-context
	 * -parameter-spring/
	 */
	

}