package com.fernandesjose.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private TokenStore tokenStore;
	
	//definindo quais requisições vão ser acessadas por quem
	private static final String[] PUBLIC = { "/oauth/token" };
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};
	private static final String[] ADMIN = {"/users/**"};

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//Configurando as requisições autorizadas a quem
		http.authorizeRequests()
		//O vetor PUBLIC essas requisição vão ser permitido por todos
		.antMatchers(PUBLIC).permitAll()
		//Apenas as requisções GET das rotas Operator_or_Admin, podem ser requisitas e permitidas por todos
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		//As rotas do vetor Operator_or_Admin podem ser acessadas apenas para perfis Operator e Admin(logados no app)
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		//Qualquer outra rota para acessar deve estar autenticado no sistema, não importando o tipo de perfil
		.anyRequest().authenticated();
	}
	
	
	
}

