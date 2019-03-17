package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Profile("oauth-security")
@Configuration // não é necessário esse @, mas ja mostra que é uma configuração
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true) // Pega os GET, POST, DELETE...
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired // se não fosse injetado, era @Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//passwordEncoder lê a senha cadastrada no V04 como $2a$10$X607ZPhQ4EgGNaYKt3n4SONjIv9zc.VMWdEuhCuba7oLAL5IvcL5.
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());	
		
		/* Movendo o usuario para o banco de dados, eu substitui esse código
		 * 
		auth.inMemoryAuthentication()
		// é no "Headers" da requisição que eu informo, todas as vezes, o usuário e a senha 
			.withUser("admin").password("admin").roles("ROLE"); */
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		/* // apenas "categorias" tem acesso liberado, sem precisar de usuário ou senha (COM A INCLUSÃO DAS PERMISSÕES, ELA É DESNECESSARIA)
				.antMatchers("/categorias").permitAll() */
				.anyRequest().authenticated()
				.and()
			/* REMOVIDO PARA IMPLEMENTAR OAuth	// modo de autenticação: Basic
			.httpBasic().and() */
			// Não tem estado de nada, logo sem exceção
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			// javascript injection
			.csrf().disable();
	}
	
	// Garante que o servidor fique STATELESS
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		// Handler para conseguir fazer a segurança dos metodos ao modelo OAuth2
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
