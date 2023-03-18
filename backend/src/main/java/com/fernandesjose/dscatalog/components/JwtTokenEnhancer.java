package com.fernandesjose.dscatalog.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.fernandesjose.dscatalog.entities.User;
import com.fernandesjose.dscatalog.repositories.UserRepository;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

	@Autowired
	private UserRepository repoUser;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		//encontrei o usuario do token
		User user = repoUser.findByEmail(authentication.getName());
		
		//Crie as informações novas para o token
		Map<String, Object> map = new HashMap<>();
		map.put("userFirstName", user.getFirstName());
		map.put("clientId", user.getId());
		
		//Agora acesse a subclasse do OAuth2AccessToken
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
		//adicione as iformações novas que fez
		token.setAdditionalInformation(map);
	    return accessToken;
		
	}

	
}
