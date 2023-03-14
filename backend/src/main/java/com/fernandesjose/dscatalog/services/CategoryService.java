package com.fernandesjose.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.CategoryDTO;
import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;

 	
@Service
public class CategoryService {
	
	@Autowired
	public CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page<Category> list = repo.findAll(pageable);
		return list.map(x -> new CategoryDTO(x));
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
