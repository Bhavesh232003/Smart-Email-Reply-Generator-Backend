package com.email.email.writer.jwt;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.email.email.writer.service.UserService;

//import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
   
	//@Autowired
    private final  JwtUtils jwtUtils;
	
	//@Autowired
    private final UserService userDetailsService;

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils, UserService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }
	
	
	private static final Logger logger=LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        logger.debug("AuthTonkenFilter called for URI : {}",request.getRequestURI());
		try {
			String jwt=jwtUtils.getJwtFromHeader(request);
			if(jwt!=null && jwtUtils.validateJwtToken(jwt)) {
				String username=jwtUtils.getUsernameFromJwtToken(jwt);
				
				UserDetails userDetails=userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken
						(userDetails,null,userDetails.getAuthorities());
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		catch(Exception e) {
			logger.error("Can't set user Authentication :{} ",e);
		}
		filterChain.doFilter(request, response);
	}
	
	
//	private String parseJwt(HttpServletRequest request) {
//		String jwt =jwtUtils.getJwtFromHeader(request);
//		logger.debug("AuthTokenFilter.java : {}",jwt);
//		return jwt;
//	}
//	
	
}


