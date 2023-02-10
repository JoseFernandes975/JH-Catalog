package com.fernandesjose.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	public CategoryRepository repo;
	
	public List<Category> findAll(){
		List<Category> list = repo.findAll();
		return list;
	}

}
