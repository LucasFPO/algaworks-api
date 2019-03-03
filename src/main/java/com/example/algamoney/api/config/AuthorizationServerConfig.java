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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter  {

	@Autowired
	private AuthenticationManager authenticationManager; // ele que pegará o usuário e senha do ResourceServerConfig
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// poderia colocar clients.jdbc (para banco de dados)
		clients.inMemory() // servirá para o cliente em Angular
			.withClient("angular") // CLIENTE, e não, USUÁRIO
			.secret("@ngul@r0") // senha CLIENTE, e não, USUÁRIO
			.scopes("read", "write") // limita o acesso. (Ex: você tem acesso à leitura e escrita)
			.authorizedGrantTypes("password", "refresh_token") // o angular recebe o usuário e senha e envia para pegar o AcessToken
			.accessTokenValiditySeconds(1800) // tempo em que o Token fica ativo (1800s:60 = 30min)
			.refreshTokenValiditySeconds(3600 * 24); // Duração de 1 dia
		
			// Depois de 30min, o Access Token é expirado, e novamente precisa se fazer uma requisição
			// com POST para receber um novo Token.
		
			// REFRESH-TOKEN: depois que o Token é expirado, eu solicito um novo Token com Refresh-Token
			// a ideia é que o usuário possa sempre dar refresh, por isso o tempo de 1 dia. Já não preciso
			// mais passar o "usuário" e "senha"
			
			// REFRESH-TOKEN RETORNA UM NOVO ACCESS TOKEN
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.tokenStore(tokenStore())
			.accessTokenConverter(accessTokenConverter()) // Adicionado com a implementação JWT
			.reuseRefreshTokens(false) // Um novo Token é gerado, não podendo reutilizar
			.authenticationManager(authenticationManager);
	}
	
	//Bloco de código adicionado com a implementação JWT
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");
		return accessTokenConverter;
	}
	
	@Bean // armazenamento do Token
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
		/* Necessário quando não tinha a implementação do JWT
		 * return new InMemoryTokenStore(); // Token armazenado inMemory */
	}
}
