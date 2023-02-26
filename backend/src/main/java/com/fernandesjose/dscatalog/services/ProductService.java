package com.fernandesjose.dscatalog.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.CategoryDTO;
import com.fernandesjose.dscatalog.dto.ProductDTO;
import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.entities.Product;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;
import com.fernandesjose.dscatalog.repositories.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private CategoryRepository catRepo;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable){
		Page<Product> list = repo.findAll(pageable);
		return list.map(x -> new ProductDTO(x));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repo.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id not found!"));
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		transformation(entity, dto);
        entity = repo.save(entity);
        return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
	try {
		Product entity = repo.getReferenceById(id);
		transformation(entity, dto);
		entity = repo.save(entity);
		return new ProductDTO(entity);
	}catch(EntityNotFoundException e) {
		throw new ResourceNotFoundException("Id não encontrado!");
	}
	}
	
	public void delete(Long id) {
		try {
			repo.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found!");
		}catch(DataIntegrityViolationException e) {
			throw new DataBaseException("Dont´t delete this product, it is associated!");
		}
	}
	
	private void transformation(Product entity, ProductDTO dto) {
		entity.setName(dto.getName());
		entity.setPrice(dto.getPrice());
		entity.setDescription(dto.getDescription());
		entity.setImgUrl(dto.getImgUrl());
		entity.setDate(dto.getDate());
		entity.getCategories().clear();
		
		for(CategoryDTO x : dto.getCategories()) {
			Category cat = catRepo.getReferenceById(x.getId());
			entity.getCategories().add(cat);
		}
		
	}
}
