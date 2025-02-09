package com.telejaca.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.telejaca.service.EmployeeService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String ADMIN = "ADMIN";

	@Bean
	EmployeeService userDetailsService() {
		return new EmployeeService();
	}
	
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> {
			requests
			.requestMatchers("/").permitAll()
			.requestMatchers("/callTypes/**").hasAuthority(ADMIN)
			.requestMatchers("/locality/**").hasAuthority(ADMIN)
			.requestMatchers("/employee").authenticated()
			.requestMatchers("/employee/edit/**").authenticated()
			.requestMatchers("/employee/**").hasAuthority(ADMIN)// Hay que descomentarlo cuando todos tengamos el usuario admin creado y pass cambiadas
			.requestMatchers("/call/callFirstStage/**").authenticated()
			.requestMatchers("/relation/add/**").authenticated()
			.requestMatchers("/user/**").authenticated()
//			.requestMatchers("/employee/changePassword").permitAll()
//			.requestMatchers("/call/callFirstStage").hasAuthority("ADMIN")
//			.requestMatchers("/error").permitAll()
			.requestMatchers("/users").denyAll()
			.requestMatchers("/phones/**").denyAll()
			.anyRequest().permitAll(); //no puede ser
		}).formLogin((form) -> form.loginPage("/login").permitAll())
				.logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/index").permitAll());
		
		return http.build();
	}
	
}
