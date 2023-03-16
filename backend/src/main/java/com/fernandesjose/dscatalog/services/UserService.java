package com.fernandesjose.dscatalog.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandesjose.dscatalog.dto.RoleDTO;
import com.fernandesjose.dscatalog.dto.UserDTO;
import com.fernandesjose.dscatalog.dto.UserInsertDTO;
import com.fernandesjose.dscatalog.dto.UserUpdateDTO;
import com.fernandesjose.dscatalog.entities.Role;
import com.fernandesjose.dscatalog.entities.User;
import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;
import com.fernandesjose.dscatalog.repositories.RoleRepository;
import com.fernandesjose.dscatalog.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAll(Pageable pageable){
		Page<User> list = repository.findAll(pageable); 
		return list.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
	    User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id Not Found!"));
	    return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		transformation(entity, dto);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		User entity = repository.getReferenceById(id);
		transformation(entity, dto);
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found!");
		}catch(DataIntegrityViolationException e) {
			throw new DataBaseException("Dont´t delete this product, it is associated!");
		}
	}
	
	private void transformation(User entity, UserDTO dto) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		entity.getRoles().clear();
		
		for(RoleDTO x : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(x.getId());
			entity.getRoles().add(role);
		}
		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		if(user == null) {
			logger.error("Email not found: " + username);
			throw new UsernameNotFoundException("Email not found!");
		}
		logger.info("Email found: "+username);
		return user;
	}
}
