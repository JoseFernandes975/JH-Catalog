package com.fernandesjose.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandesjose.dscatalog.dto.ProductDTO;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.services.ProductService;
import com.fernandesjose.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourcesTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMap;
	
	private Long idExists;
	private Long nonExistsId;
	private Long dependentId;
	private ProductDTO dto;
	private PageImpl page;
	
	@BeforeEach
	void setUp() {
		idExists = 1L;
		nonExistsId = 1000L;
		dependentId = 4L;
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(dto));
		
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(dto);
		
		
		Mockito.when(service.findAll(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(idExists)).thenReturn(dto);
		Mockito.when(service.findById(nonExistsId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.update(eq(idExists), any())).thenReturn(dto);
		Mockito.when(service.update(eq(nonExistsId), any())).thenThrow(ResourceNotFoundException.class);
		
	    Mockito.doNothing().when(service).delete(idExists);
	    Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistsId);
	    Mockito.doThrow(DataBaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	public void insertShouldReturnCreated() throws Exception {
		String jsonBody = objectMap.writeValueAsString(dto);
		//o metodo perform faz uma requisição
	mockMvc.perform(post("/products").content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()); 
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", idExists)).andExpect(status().isNoContent());
		
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", nonExistsId)).andExpect(status().isNotFound());
	}
	
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
		
	    String jsonBody = objectMap.writeValueAsString(dto);
	    //Perform requisição tipo PUT, caminho, conteudo(corpo), tipo da resposta e espera o status 200
        mockMvc.perform(put("/products/{id}", idExists).content(jsonBody)
        		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        
        jsonPath("$.id").exists();
        jsonPath("$.name").exists();
	}
	
	@Test
	public void updateShouldThrowNotFoundWhenIdNotFound() throws Exception {
		String jsonBody = objectMap.writeValueAsString(dto);
	    mockMvc.perform(put("/products/{id}", nonExistsId).content(jsonBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	
	@Test
	public void findByIdShouldReturnProductDto() throws Exception {
		mockMvc.perform(get("/products/{id}", idExists)).andExpect(status().isOk());
		jsonPath("$.id").exists();
		jsonPath("$.name").exists();
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdNotExists() throws Exception {
		mockMvc.perform(get("/products/{id}", nonExistsId)).andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().isOk());
	}
}
