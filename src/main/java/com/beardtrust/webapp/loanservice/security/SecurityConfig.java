package com.beardtrust.webapp.loanservice.security;

import com.beardtrust.webapp.userservice.services.AuthenticationService;
import com.beardtrust.webapp.userservice.services.AuthorizationService;
import com.beardtrust.webapp.userservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The application's security configuration class.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final Environment environment;
	private final AuthorizationService authorizationService;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	public SecurityConfig(Environment environment, AuthorizationService authorizationService) {
		this.environment = environment;
		this.authorizationService = authorizationService;
	}

	@Description("Configure HTTP Security")
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.cors()
				.and().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/loans/**").permitAll()
				.antMatchers(HttpMethod.POST, "/loantypes/**").permitAll()
				.and().authorizeRequests().anyRequest().authenticated()
				.and()
				.addFilter(new AuthorizationFilter(authenticationManager(), environment, authorizationService));
		http.headers().frameOptions().disable();
	}
}
