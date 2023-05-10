package com.fernandesjose.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private TokenStore tokenStore;
	
	//definindo quais requisições vão ser acessadas por quem
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};
	private static final String[] ADMIN = {"/users/**"};

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//Mostrar o h2
		//No ambiente da aplicação, se os profiles que estiver sendo executado e for test
		//desabilitar os frames pois a interface do h2 quer que desbilite os frames
		if(Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		
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
		
		http.cors().configurationSource(corsConfigurationSource());
		
	}
		@Bean
		public CorsConfigurationSource corsConfigurationSource() {
		    CorsConfiguration corsConfig = new CorsConfiguration();
		 //origens permitidas(Allowed Origin Patterns)
		    //Ou seja mais para frente você quer liberar para o front end, então vai trocar o * por exemplo: htt*://meudominio.com
		   corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
		   //Metodos que quer liberar
		    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
		    //Credenciais
		    corsConfig.setAllowCredentials(true);
		    //Quais cabeçalhos que quer permitir.
		    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		 
		    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		    source.registerCorsConfiguration("/**", corsConfig);
		    return source;
		}
		 //Esse componente aqui é um auxiliar para registrar sua configuração de CORS
		@Bean
		public FilterRegistrationBean<CorsFilter> corsFilter() {
		    FilterRegistrationBean<CorsFilter> bean
		            = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
		    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		    return bean;
		}
	
	
}

