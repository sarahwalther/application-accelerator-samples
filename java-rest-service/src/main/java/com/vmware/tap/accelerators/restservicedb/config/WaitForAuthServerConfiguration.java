package com.vmware.tap.accelerators.restservicedb.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Blocks until the authorization server is ready. The AuthServer being accessible is
 * required for OAuth2 clients to be configured. If it is not accessible, the Boot app
 * crashes.
 *
 * This configuration class, being user-provided, is wired be wired in before any
 * auto-configuration class, and blocks the full application.
 */
@Configuration
public class WaitForAuthServerConfiguration {

	public static final String OPENID_CONFIGURATION = "/.well-known/openid-configuration";

	WaitForAuthServerConfiguration(OAuth2ResourceServerProperties props) throws InterruptedException {
		var logger = LoggerFactory.getLogger("Authorisation Server Readiness");
		var httpClient = new RestTemplate();
		// @formatter:off
		var openIDConfigURI = Optional.ofNullable(props.getJwt().getIssuerUri())
				.map(this::getConfigUriOrThrow)
				.orElseThrow(() -> new RuntimeException("No spring.security.oauth2.client.provider[*].issuer-uri is set"));
		// @formatter:on

		while (true) {
			try {
				if (httpClient.getForEntity(openIDConfigURI, Object.class).getStatusCode().is2xxSuccessful()) {
					break;
				}
			}
			catch (RestClientException e) {
				logger.error("Authorisation server is not yet ready. Got {}", e.getMessage());
			}
			Thread.sleep(1000);
		}
		logger.info("Authorisation server is ready!");
	}

	private URI getConfigUriOrThrow(String iss) {
		try {
			var issuerURI = new URI(iss);
			if (!issuerURI.isAbsolute()) {
				throw new RuntimeException(issuerURI + " is not an absolute URI");
			}
			return issuerURI.resolve(OPENID_CONFIGURATION);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(iss + " is not a valid URI");
		}
	}

}
