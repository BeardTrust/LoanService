package com.beardtrust.webapp.loanservice.security;

import com.beardtrust.webapp.loanservice.dtos.UserDTO;
import com.beardtrust.webapp.loanservice.services.AuthorizationService;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Authorization filter.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {
	private final Environment environment;
	private final AuthorizationService authorizationService;

	/**
	 * Instantiates a new Authorization filter.
	 *
	 * @param authenticationManager the authentication manager
	 * @param environment           the environment
	 * @param authorizationService  the authorization service
	 */
	@Autowired
	public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment,
							   AuthorizationService authorizationService) {
		super(authenticationManager);
		this.environment = environment;
		this.authorizationService = authorizationService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain) throws IOException, ServletException {
		String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

		if (authorizationHeader != null && authorizationHeader.startsWith(environment.getProperty("authorization" +
				".token.header.prefix"))) {
			log.info("Filtering new request");
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
			log.error("Incoming request missing required components");
		}
		chain.doFilter(request, response);
	}

	/**
	 * This function builds the authentication token to be used in authorization.
	 *
	 * @param request
	 * @return
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		log.info("Validating requester authorization");
		String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
		UsernamePasswordAuthenticationToken authenticationToken = null;
		if (authorizationHeader != null) {
			String token = authorizationHeader.replace(environment.getProperty("authorization.token" +
					".header.prefix") + " ", "");

			String userId = Jwts.parser()
					.setSigningKey(environment.getProperty("token.secret"))
					.parseClaimsJws(token)
					.getBody()
					.getSubject();

			if (userId != null) {
				UserDTO userDTO = authorizationService.getUserByUserId(userId);

				if (userDTO != null) {
					List<GrantedAuthority> authorities = new ArrayList<>();

					if (userDTO.getRole().equals("admin")) {
						SimpleGrantedAuthority admin = new SimpleGrantedAuthority("admin");
						authorities.add(admin);
					} else if (userDTO.getRole().equals("user")) {
						SimpleGrantedAuthority user = new SimpleGrantedAuthority("user");
						authorities.add(user);
					}

					authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
				}
			} else {
				log.error("Unable to validate requester's authorization");
			}
		}
		return authenticationToken;
	}
}
