package com.fernandesjose.dscatalog.services;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.CategoryDTO;
import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

 	
@Service
public class CategoryService {
	
	@Autowired
	public CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repo.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).toList();
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repo.findById(id);
	    Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id não encontrado!"));
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insertCategory(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		repo.save(entity);
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
		try {
		Category entity = repo.getReferenceById(id);
		entity.setName(dto.getName());
		entity = repo.save(entity);
		return new CategoryDTO(entity);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found");
		}
	}
	
	
	public void deleteCategory(Long id) {
		try {
		repo.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("iD not found!");
		}catch(DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation, object associate!");
		}
	}

}
