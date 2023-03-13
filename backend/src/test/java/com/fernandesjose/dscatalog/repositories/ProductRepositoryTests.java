package com.fernandesjose.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.fernandesjose.dscatalog.entities.Product;
import com.fernandesjose.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long idExist;
	private long idNotExist;
	private long totalIdProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		idExist = 1L;
		idNotExist = 1000L;
		totalIdProducts = 25L;
	}
	
	@Test
	public void saveShouldAutoCompletionWhenIdIsNull() {
		Product product = Factory.createProduct();
		
		product.setId(null);
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(totalIdProducts + 1, product.getId());
		
	}
	
	@Test
	public void findByIdShouldReturnNoEmptyOptionalWhenIdExists() {
		 Optional<Product> result = repository.findById(idExist);
		 
		 Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnOptionalEmptyWhenIdNotExists() {
		Optional<Product> result = repository.findById(idNotExist);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(idExist);
		Optional<Product> result = repository.findById(idExist);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			
			repository.deleteById(idNotExist);
		});
	}

}
