package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter  {

	@Autowired
	private AuthenticationManager authenticationManager; // ele que pegará o usuário e senha do ResourceServerConfig
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// poderia colocar clients.jdbc (para banco de dados)
		clients.inMemory() // servirá para o cliente em Angular
			.withClient("angular")
			.secret("@ngul@r0")
			.scopes("read", "write") // limita o acesso. (Ex: você tem acesso à leitura e escrita)
			.authorizedGrantTypes("password") // o angular recebe o usuário e senha e envia para pegar o AcessToken
			.accessTokenValiditySeconds(1800); // tempo em que o Token fica ativo (1800s:60 = 30min)
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.tokenStore(tokenStore())
			.authenticationManager(authenticationManager);
	}
	
	@Bean // armazenamento do Token
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
}
