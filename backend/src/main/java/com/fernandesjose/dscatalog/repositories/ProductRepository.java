package com.fernandesjose.dscatalog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandesjose.dscatalog.entities.Category;
import com.fernandesjose.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	@Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats "
			+ "WHERE (:category IS NULL OR :category = cats)")
			Page<Product> find(Category category, Pageable pageable);

}
