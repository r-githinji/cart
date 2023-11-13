package com.example.cart;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.cart.*;

@SpringBootTest(classes = {CartApplication.class})
public class CartApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(CartApplicationTests.class);
	@Autowired
	private ItemRepository catalog;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private SaleService sleSvc;
	@Autowired
	private CustomerRepository cusRepo;
	
	Customer createBuyer() {
		Customer debtor = new Customer(cusRepo.series());
		debtor.setFirstName("Online");
		debtor.setLastName("Customer");
		debtor.setGender(GenderType.FEMALE);
		debtor.setEmail("online.customer@domain.com");
		return debtor;
	}
	
	Sale createCart(Customer debtor) {
		//playstation 4
		Item item = catalog.findById(1);
		SaleLine line = new SaleLine(item, 2);
		//credit sale
		Sale cart = new Sale(SaleType.CREDIT, sleSvc.series());
		cart.setDebtor(debtor);
		cart.put(line);
		cart.aggregate();
		//sony bravia tv
		item = catalog.findById(2);
		line = new SaleLine(item, 1);
		cart.put(line);
		cart.aggregate();
		//samsung smart watch
		item = catalog.findById(3);
		line = new SaleLine(item, 3);
		cart.put(line);
		cart.aggregate();
		return cart;
	}
	
	Address createShipping(Sale sale) {
		Address shipto = new Address();
		shipto.setStreet("Delivery Street");
		shipto.setAvenue("Delivery Avenue");
		shipto.setHouseNo("House No");
		shipto.setCity("City Or Town");
		shipto.setRemember(false);
		sale.setShipTo(shipto);
		return shipto;
	}
	
	@Test
	public void contextLoads() {
		Customer buyer = createBuyer();
		//create cart with debtor
		Sale cart = createCart(buyer);		
		//create shipping address
		createShipping(cart);
		//save cart
		cart = sleSvc.create(cart);		
		//verify double entry sub-system is working as expected
		Account receivable = accRepo.find("1005");
		assertTrue((cart.getGross() - receivable.getBalance()) == 0);
				
		Account sales = accRepo.find("1007");
		assertTrue((cart.getNett() - sales.getBalance()) == 0);
				
		Account tax = accRepo.find("1018");
		assertTrue((cart.getTax() - tax.getBalance()) == 0);
		
		//simulate sales return for credit sale
		sleSvc.credit(cart);
		receivable = accRepo.find("1005");
		assertTrue(receivable.getBalance() == 0);
		
		//delete cart
		sleSvc.delete(cart);
	}
}
