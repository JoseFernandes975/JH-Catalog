package com.fernandesjose.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.CategoryDTO;
import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	public CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repo.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).toList();
	}

}
