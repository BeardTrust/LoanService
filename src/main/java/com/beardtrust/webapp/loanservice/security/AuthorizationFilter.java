package com.beardtrust.webapp.loanservice.security;

import com.beardtrust.webapp.cardservice.dtos.UserDTO;
import com.beardtrust.webapp.cardservice.services.AuthorizationService;
import io.jsonwebtoken.Jwts;
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

public class AuthorizationFilter extends BasicAuthenticationFilter{
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

	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain) throws IOException, ServletException {
		System.out.println("doFilterInternal()");
		String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

		if (authorizationHeader != null && authorizationHeader.startsWith(environment.getProperty("authorization" +
				".token.header.prefix"))) {
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
		}
		chain.doFilter(request, response);
	}
	
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		System.out.println("getAuthentication()");
		String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
		UsernamePasswordAuthenticationToken authenticationToken = null;
		if (authorizationHeader != null) {
			System.out.println("authorizationHeader != null");
			String token = authorizationHeader.replace(environment.getProperty("authorization.token" +
					".header.prefix") + " ", "");

			String userId = Jwts.parser()
					.setSigningKey(environment.getProperty("token.secret"))
					.parseClaimsJws(token)
					.getBody()
					.getSubject();

			if (userId != null) {
				System.out.println("userId != null");
				UserDTO userDTO = authorizationService.getUserByUserId(userId);

				if (userDTO != null) {
					System.out.println("userDTO != null");
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
				//log.error("Unable to validate requester's authorization");
			}
		}
		return authenticationToken;
	}
}

