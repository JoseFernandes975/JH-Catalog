package com.fernandesjose.dscatalog.resources.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fernandesjose.dscatalog.exceptions.DataBaseException;
import com.fernandesjose.dscatalog.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request){
		StandardError err = new StandardError();
		HttpStatus status = HttpStatus.NOT_FOUND;
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Entity Not Found!");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DataBaseException.class)
	public ResponseEntity<StandardError> database(DataBaseException e, HttpServletRequest request){
		StandardError err = new StandardError();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("DataBase Exception!");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
}
