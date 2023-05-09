package com.fernandesjose.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.ProductDTO;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.ProductRepository;

@SpringBootTest
@Transactional
public class ProdutServiceIT {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existsId;
	private Long nonExistsId;
	private Long countTotal;
	private Long idCategory;
	private Long idNotExistCategory;
	
	
	@BeforeEach
	void setUp() {
		existsId = 1L;
		nonExistsId = 1000L;
		countTotal = 25L;
		idCategory = 3L;
		idNotExistCategory = 5L;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existsId);
		
		Assertions.assertEquals(countTotal - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});
	}
	/*
	@Test
	public void findAllPageShouldReturnPageWhenPage0Size10() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAll(pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotal, result.getTotalElements());
	    Assertions.assertEquals(3, result.getTotalPages());
	}
	
	@Test
	public void findAllPageShouldReturnPageEmptyWhenPageDoesNotExists() {
		Pageable pageable = PageRequest.of(50, 22);
		
		Page<ProductDTO> result = service.findAll(pageable);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPageShouldSortWhenSortedByName() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAll(pageable);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
	*/
}
