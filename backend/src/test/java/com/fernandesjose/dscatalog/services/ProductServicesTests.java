package com.fernandesjose.dscatalog.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fernandesjose.dscatalog.dto.ProductDTO;
import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.entities.Product;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;
import com.fernandesjose.dscatalog.repositories.ProductRepository;
import com.fernandesjose.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {
	
	private long idExists;
	private long nonExistsId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	private ProductDTO productDTO;
	
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock 
	private CategoryRepository categoryRepo;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;
		nonExistsId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		
		
		//Configurando um comportamento do repository mockado
		
		
		Mockito.when(categoryRepo.getReferenceById(idExists)).thenReturn(category);
		Mockito.when(categoryRepo.getReferenceById(nonExistsId)).thenThrow(EntityNotFoundException.class); 
		
		
		Mockito.when(repository.getReferenceById(idExists)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistsId)).thenThrow(EntityNotFoundException.class);
	    
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(idExists)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.find(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(page);
		
		
		
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.doNothing().when(repository).deleteById(idExists);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistsId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {
		
	     ProductDTO result = service.update(idExists, productDTO);
	    
	    Assertions.assertNotNull(result);
	    Assertions.assertEquals("Phone", product.getName());
	    verify(repository, times(1)).save(product);
	    verify(categoryRepo, times(1)).getReferenceById(2L);
	    verify(repository, times(1)).getReferenceById(product.getId());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistsId, productDTO);
		});
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(idExists);
		
		Assertions.assertEquals(1l, result.getId());
	    verify(repository).findById(idExists);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		   Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			  
			   ProductDTO result = service.findById(nonExistsId);
			   
			   Mockito.verify(repository).findById(nonExistsId);
			   
		   });
	}
	
	@Test
	public void findAllShouldReturnPage() {
	   Pageable pageable = PageRequest.of(0, 10);
	   
	   Page<ProductDTO> result = service.findAll(0L, "", pageable);
	   
	   Assertions.assertNotNull(result);
	}
	
	@Test 
	public void deleteShouldThrowDataBaseExceptionWhenIdDependency() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistsId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(idExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idExists);
	}
	
}
