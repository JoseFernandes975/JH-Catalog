package com.fernandesjose.dscatalog.resources;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandesjose.dscatalog.dto.ProductDTO;
import com.fernandesjose.dscatalog.tests.Factory;
import com.fernandesjose.dscatalog.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objMapp;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private String clientUsername;
	private String clientPassword;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
	    .accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		 jsonPath("$.totalElements").value(countTotalProducts);
         jsonPath("$.content[0].name").value("Macbook Pro");
		 jsonPath("$.content[1].name").value("PC Gamer");
	     jsonPath("$.content[2].name").value("PC Gamer Alfa");
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
		ProductDTO product = Factory.createProductDTO();
		
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		String jsonBody = objMapp.writeValueAsString(product);
		String expectedName = product.getName();
		
		mockMvc.perform(put("/products/{id}", existingId)
			.header("Authorization", "Bearer " + accessToken)
			 .accept(MediaType.APPLICATION_JSON).content(jsonBody)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		jsonPath("$.id").exists();
		jsonPath("$.name").value(expectedName);
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ProductDTO product = Factory.createProductDTO();
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		
		String jsonBody = objMapp.writeValueAsString(product);
		
		mockMvc.perform(put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}
