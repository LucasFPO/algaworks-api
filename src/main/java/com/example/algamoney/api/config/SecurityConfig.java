package com.example.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration // não é necessário esse @, mas ja mostra que é uma configuração
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		// é no "Headers" da requisição que eu informo, todas as vezes, o usuário e a senha 
			.withUser("admin").password("admin").roles("ROLE");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		// apenas "categorias" tem acesso liberado, sem precisar de usuário ou senha
				.antMatchers("/categorias").permitAll()
				.anyRequest().authenticated()
				.and()
				// modo de autenticação: Basic
			.httpBasic().and()
			// Não tem estado de nada, logo sem exceção
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			// javascript injection
			.csrf().disable();
	}
}
