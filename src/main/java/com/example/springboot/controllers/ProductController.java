package com.example.springboot.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;


import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController
public class ProductController {

	
	@Autowired
	ProductRepository productRepository;
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto ){
		var productModel = new ProductModel();
		//recebe um tipo no caso um texto "json"a partir do DTO e converte para o modelo -> productModel
		BeanUtils.copyProperties(productRecordDto, productModel);
		// envia para o cliente um status 201 criado se for criado
		// no corpo da resposta envia o nome,id e valor
		//e com o repository pega o productModel e salva na base de dados usando o metodo save
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
		
	}
	
	
	/*/@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
	}*/
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		
		List<ProductModel> productList = productRepository.findAll();
	
		if(!productList.isEmpty()) {
			for(ProductModel product : productList ) {
				
				Long id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}
	
	
	
	
	@GetMapping("/products/{id}") 
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") Long id){
		Optional<ProductModel> product = productRepository.findById(id);
		if(product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
		}
		
		product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Produtos"));
		
		return ResponseEntity.status(HttpStatus.OK).body(product.get());
	}
	
	
	
	
	@PutMapping("/products/{id}") 
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") Long id,
												@RequestBody @Valid ProductRecordDto productRecordDto){
		Optional<ProductModel> product = productRepository.findById(id);
		if(product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found");
			
		}
		var productModel = product.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") Long id){
		
		Optional<ProductModel> product = productRepository.findById(id);
		if(product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
			
		} 
		productRepository.delete(product.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
		
	}
	
	
	
}
