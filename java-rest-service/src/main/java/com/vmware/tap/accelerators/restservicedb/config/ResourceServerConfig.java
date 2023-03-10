package com.vmware.tap.accelerators.restservicedb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

	// @formatter:off
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(authorizeRequests -> {
					authorizeRequests.antMatchers(HttpMethod.GET, "/").hasAuthority("SCOPE_message.read");
					authorizeRequests.antMatchers(HttpMethod.GET, "/customer-profiles/**").hasAuthority("SCOPE_message.read");
					authorizeRequests.antMatchers(HttpMethod.POST, "/").hasAuthority("SCOPE_message.write");
					authorizeRequests.antMatchers(HttpMethod.POST, "**").hasAuthority("SCOPE_message.write");
					authorizeRequests.antMatchers(HttpMethod.PATCH, "**").hasAuthority("SCOPE_message.write");
					authorizeRequests.antMatchers(HttpMethod.DELETE, "**").hasAuthority("SCOPE_message.write");
					authorizeRequests.anyRequest().denyAll();//?
				})
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.build();
	}
	// @formatter:on

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		logger.info("Authorization server issuer URI: " + issuerUri);
	}
}
