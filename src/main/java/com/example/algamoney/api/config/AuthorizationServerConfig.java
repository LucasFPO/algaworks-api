package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

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
			.scopes("read", "write") // limita o acesso. (Ex: você tem acesso à leitura e escrita) Ver CategoriaResource.java
			.authorizedGrantTypes("password", "refresh_token") // o angular recebe o usuário e senha e envia para pegar o AcessToken
			.accessTokenValiditySeconds(1800) // tempo em que o Token fica ativo (1800s:60 = 30min)
			.refreshTokenValiditySeconds(3600 * 24) // Duração de 1 dia
		
			// Depois de 30min, o Access Token é expirado, e novamente precisa se fazer uma requisição
			// com POST para receber um novo Token.
		
			// REFRESH-TOKEN: depois que o Token é expirado, eu solicito um novo Token com Refresh-Token
			// a ideia é que o usuário possa sempre dar refresh, por isso o tempo de 1 dia. Já não preciso
			// mais passar o "usuário" e "senha"
			
			// REFRESH-TOKEN RETORNA UM NOVO ACCESS TOKEN
		
		// As permissões para o cliente "angular" nem sempre são as mesmas para o "mobile", vide scopes 
		.and()
			.withClient("mobile")
			.secret("m0b1l30")
			.scopes("read")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(1800)
			.refreshTokenValiditySeconds(3600 * 24);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// criação de um token mais detalhado, que mostra o nome do usuário
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain) // Adicionado com a implementação JWT
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
		// o JwtTokenStore ele não armazena, na verdade ele apenas valida já que em REST, ele deve ser STATELESS (não guarda o estado)
		return new JwtTokenStore(accessTokenConverter());
		/* Necessário quando não tinha a implementação do JWT
		 * return new InMemoryTokenStore(); // Token armazenado inMemory */
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
	    return new CustomTokenEnhancer();
	}
}


