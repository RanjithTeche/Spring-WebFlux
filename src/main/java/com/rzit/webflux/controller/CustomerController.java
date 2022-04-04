package com.rzit.webflux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers/v1/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@GetMapping("/{city}")
	public Flux<CustomerVO> getCustomersWithInCity(@PathVariable String city) {
		return customerService.findAllCustomerInCity(city);
	}

	@GetMapping()
	public Flux<CustomerVO> getAllCustomers() {
		return customerService.findAll();
	}

	@PostMapping
	public Mono<CustomerVO> saveCustomer(@RequestBody CustomerVO customer) {
		return customerService.createCustomer(customer);
	}

	@PutMapping("/{id}")
	public Mono<CustomerVO> updateCustomer(@PathVariable String id, @RequestBody CustomerVO customer) {
		return customerService.updateCustomer(id, customer);
	}

	@DeleteMapping("/{id}")
	public Mono<Void> deleteCustomer(@PathVariable String id) {
		return customerService.deleteCustomer(id);
	}

}

interface CustomerService {
	Flux<CustomerVO> findAllCustomerInCity(String city);

	Mono<Void> deleteCustomer(String id);

	Mono<CustomerVO> updateCustomer(String id, CustomerVO customer);

	Mono<CustomerVO> createCustomer(CustomerVO customer);

	Flux<CustomerVO> findAll();
}

@Service
class CustomerServiceImpl implements CustomerService {
	@Autowired
	CustomerRepository repository;

	public Flux<CustomerVO> findAllCustomerInCity(String city) {
		Flux<Customer> customers = repository.findByCity(city);

		return customers.map(c -> toVO(c));
	}

	public Flux<CustomerVO> findAll() {
		Flux<Customer> customers = repository.findAll();

		return customers.map(c -> toVO(c));
	}

	@Override
	public Mono<CustomerVO> createCustomer(CustomerVO customerVO) {
		Flux<Customer> customer = repository.saveAll(Mono.just(toEntity(customerVO)));
		return customer.single().map(c -> toVO(c));
	}

	@Override
	public Mono<CustomerVO> updateCustomer(String id, CustomerVO customerVO) {
		Customer entity = toEntity(customerVO);
		entity.setId(id);
		Flux<Customer> customer = repository.saveAll(Mono.just(entity));
		return customer.single().map(c -> toVO(c));
	}

	@Override
	public Mono<Void> deleteCustomer(String id) {
		Mono<Void> customer = repository.deleteById(Mono.just(id));
		return customer;
	}

	private Customer toEntity(CustomerVO vo) {
		if (vo == null) {
			return null;
		}
		Customer entity = new Customer();
		entity.setName(vo.getName());
		entity.setGender(vo.getGender());
		entity.setDateOfBirth(vo.getDateOfBirth());
		entity.setCity(vo.getCity());
		return entity;
	}

	private CustomerVO toVO(Customer entity) {
		if (entity == null) {
			return null;
		}
		CustomerVO vo = new CustomerVO();
		vo.setId(entity.getId());
		vo.setName(entity.getName());
		vo.setGender(entity.getGender());
		vo.setDateOfBirth(entity.getDateOfBirth());
		vo.setCity(entity.getCity());
		return vo;
	}

}

@Repository
interface CustomerRepository extends ReactiveCrudRepository<Customer, String> {

	Flux<Customer> findByCity(String city);

	Mono<Customer> findFirstById(String id);
}

@Document(collection = "customers")
class Customer {

	@Id
	private String id;
	private String name;
	private String dateOfBirth;
	private String gender;
	private String city;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}

class CustomerVO {
	private String id;
	private String name;
	private String dateOfBirth;
	private String gender;
	private String city;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}