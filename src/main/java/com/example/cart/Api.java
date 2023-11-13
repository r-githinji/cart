package com.example.cart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.hateoas.Link;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;

public abstract class Api {
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/catalog", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class CatalogApi {
		
		private ItemRepository itmRepo;
		private ResourceLoader loader;
		@Autowired
		public CatalogApi(ItemRepository itmRepo, ResourceLoader loader) {
			this.itmRepo = itmRepo;	
			this.loader = loader;		
		}
		
		@GetMapping(path = "/image/{id}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
		public @ResponseBody byte[] image(@PathVariable Integer id) throws Exceptions.ImageNotFoundException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Resource resource = loader.getResource(String.format("classpath:images/%s.png", id));
			try (InputStream is = resource.getInputStream()) {
				BufferedImage image = ImageIO.read(is);		
				ImageIO.write(image, "png", baos);
			} catch (IOException ioex) {
				throw new Exceptions.ImageNotFoundException(ioex);
			}
			return baos.toByteArray();
		}
		
		@GetMapping(path = "/find/{id}")
		public ResponseEntity<Item> find(@PathVariable Integer id) {
			return ResponseEntity.ok(itmRepo.findById(id));
		}
		
		@GetMapping(path = "/count")
		public ResponseEntity<Integer> count() {
			return ResponseEntity.ok(itmRepo.count());
		}
		
		@GetMapping(path = "/list/{offset}/{limit}")
		public ResponseEntity<List<Item>> list(@PathVariable Integer offset, @PathVariable Integer limit) {
			return ResponseEntity.ok(itmRepo.list(offset, limit));
		}
	}
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/cart", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class CartApi {
		
		private SaleService sleSvc;
		@Autowired
		public CartApi(SaleService sleSvc) {
			this.sleSvc = sleSvc;
		}
		
		@GetMapping(path = "/find/{id}")
		public ResponseEntity<Sale> find(@PathVariable Integer id) {
			return ResponseEntity.ok(sleSvc.findById(id));
		}
		
		@GetMapping(path = "/create")
		public ResponseEntity<Sale> create() {
			Sale cart = new Sale(SaleType.CREDIT, sleSvc.series());
			return ResponseEntity.ok(cart);
		}
		
		@PostMapping(path = "/checkout")
		public ResponseEntity<?> checkout(@Valid @RequestBody Sale sale) {
			Sale created = sleSvc.create(sale);
			Link link = linkTo(methodOn(CartApi.class).find(created.getId())).withSelfRel().expand(created.getId());
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION);
			return ResponseEntity.created(link.toUri()).headers(headers).body(created);
		}
		
		@GetMapping(path = "/count")
		public ResponseEntity<Integer> count() {
			return ResponseEntity.ok(sleSvc.count());
		}
		
		@GetMapping(path = "/list/{offset}/{limit}")
		public ResponseEntity<List<Sale>> list(@PathVariable Integer offset, @PathVariable Integer limit) {
			return ResponseEntity.ok(sleSvc.list(offset, limit));
		}
	}
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/customer", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class CustomerApi {
		
		private CustomerRepository cusRepo;
		@Autowired
		public CustomerApi(CustomerRepository cusRepo) {
			this.cusRepo = cusRepo;
		}
		
		@GetMapping(path = "/create")
		public ResponseEntity<Customer> create() {
			Customer cust = new Customer(cusRepo.series());
			return ResponseEntity.ok(cust);
		}
		
		@GetMapping(path = "/genders")
		public ResponseEntity<Map<GenderType, String>> genders() {
			Map<GenderType, String> genders = List.of(GenderType.values()).stream().collect(Collectors.toMap(Function.identity(), GenderType::getLabel));
			return ResponseEntity.ok(genders);
		}
	}
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/address", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class AddressApi {
		
		@GetMapping(path = "/create")
		public ResponseEntity<Address> create() {
			Address address = new Address();
			return ResponseEntity.ok(address);
		}
	}
	
	@CrossOrigin(origins = {"localhost:3000"})
	@RestController
	@RequestMapping(path = "/api/phone", produces = {MediaType.APPLICATION_JSON_VALUE})
	public static class PhoneApi {
		
		private CountryRepository ctryRepo;
		@Autowired
		public PhoneApi(CountryRepository ctryRepo) {
			this.ctryRepo = ctryRepo;
		}
		
		@GetMapping(path = "/create")
		public ResponseEntity<Phone> create() {
			Phone phone = new Phone();
			return ResponseEntity.ok(phone);
		}
		
		@GetMapping(path = "/codes")
		public ResponseEntity<List<Country>> codes() {
			int count = ctryRepo.count();
			return ResponseEntity.ok(ctryRepo.list(0, count));
		}
	}
}
