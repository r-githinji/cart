package com.example.cart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyAccessor;

@Transactional @Service
public class SaleService {
	
	private SaleRepository sleRepo;	
	private CustomerRepository cusRepo;
	private AddressRepository addRepo;
	private JournalRepository jnlRepo;
	private AccountRepository accRepo;
	@Autowired
	public SaleService(SaleRepository sleRepo, CustomerRepository cusRepo, AddressRepository addRepo, 
		JournalRepository jnlRepo, AccountRepository accRepo) {
		this.sleRepo = sleRepo;
		this.cusRepo = cusRepo;
		this.addRepo = addRepo;
		this.jnlRepo = jnlRepo;
		this.accRepo = accRepo;
	}
	
	@PostConstruct
	public void onCreate() {
	}
	
	public String series() {
		return sleRepo.series();
	}

	public Sale create(Sale sale) {
		if (null != sale.getId()) {
			Growl growl = Growl.of("warn", "Sale already exists!");
			throw new Exceptions.ApplicationException(growl);
		}
		Journal journal = new Journal(JournalType.SALE, sale.getReference());
		Entry entry;
		switch (sale.getType()) {
			case CASH://cash sale
				Account cash = accRepo.find("1001");
				entry = Entry.of(EntryType.DEBIT, cash, sale.getGross());				
			break;
			case CREDIT://credit sale
				Account receivable = accRepo.find("1005");
				entry = Entry.of(EntryType.DEBIT, receivable, sale.getGross());
			break;
			default://not supported
				Growl growl = Growl.of("warn", "sale type not supported");
				throw new Exceptions.ApplicationException(growl);
		}
		journal.put(entry);
		//credit sales account
		Account sales = accRepo.find("1007");
		entry = Entry.of(EntryType.CREDIT, sales, sale.getNett());
		journal.put(entry);
		//credit tax account
		Account taxes = accRepo.find("1018");
		entry = Entry.of(EntryType.CREDIT, taxes, sale.getTax());
		journal.put(entry);
		//check if journal is balanced
		if (!journal.isBalanced()) {
			Growl growl = Growl.of("error", "this sale transaction is out of balance");
			throw new Exceptions.ApplicationException(growl);
		}
		Customer debtor = sale.getDebtor();
		if (null == debtor.getId()) {
			debtor = cusRepo.create(debtor);
			sale.setDebtor(debtor);
		}
		Address shipping = sale.getShipTo();
		if (null == shipping.getId()) {
			List<Address> addresses = debtor.getAddresses();
			if (null == addresses) {
				addresses = List.of();
			}
			int max = addresses.stream().mapToInt(Address::getSequence).max().orElse(-1);
			max += 1;
			shipping.setSequence(max);
			shipping.setOwner(debtor);
			shipping = addRepo.create(shipping);
			sale.setShipTo(shipping);
		}
		sale.setPostedOn(LocalDateTime.now());
		Sale created = sleRepo.create(sale);
		//
		journal = jnlRepo.create(journal);
		//update status to paid if it's a cash sale
		switch (created.getType()) {
			case CASH:
				Map<String, ?> params = Map.of("status", SaleStatus.PAID);
				if (sleRepo.update(created, params)) {
					PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(created);
					for (Map.Entry<String, ?> tuple: params.entrySet()) {
						accessor.setPropertyValue(tuple.getKey(), tuple.getValue());
					}
				}
			break;
			default:
				//do nothing
		}
		return created; 
	}
	
	public void credit(Sale sale) {
		switch (sale.getType()) {
			case CASH:
				Growl growl = Growl.of("info", "Goods once sold cannot be returned!");
				throw new Exceptions.ApplicationException(growl);
			default:
				//do nothing
		}
		Account receivable = accRepo.find("1005");
		if (sale.getStatus() == SaleStatus.RETURN) {
			Growl growl = Growl.of("info", String.format("This sale %s has already been credited!", sale.getReference()));
			throw new Exceptions.ApplicationException(growl);
		} else if (receivable.getBalance() == 0.0) {
			Growl growl = Growl.of("info", "Please reverse receipt for this payment!");
			throw new Exceptions.ApplicationException(growl);
		}
		Journal journal = new Journal(JournalType.SALE_RETURN, sale.getReference());
		//credit acc receivable
		Entry entry = Entry.of(EntryType.CREDIT, receivable, sale.getGross());
		journal.put(entry);
		//debit sales account
		Account sales = accRepo.find("1007");
		entry = Entry.of(EntryType.DEBIT, sales, sale.getNett());
		journal.put(entry);
		//debit tax account
		Account taxes = accRepo.find("1018");
		entry = Entry.of(EntryType.DEBIT, taxes, sale.getTax());
		journal.put(entry);
		//check if journal is balanced
		if (!journal.isBalanced()) {
			Growl growl = Growl.of("error", "This journal is out of balance!");
			throw new Exceptions.ApplicationException(growl);
		}
		journal = jnlRepo.create(journal);
		//flag sale status to returned
		Map<String, ?> params = Map.of("status", SaleStatus.RETURN);
		if (!sleRepo.update(sale, params)) {
			Growl growl = Growl.of("error", "status updated failed");
			throw new Exceptions.ApplicationException(growl);
		}
		PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(sale);
		for (Map.Entry<String, ?> kv: params.entrySet()) {
			accessor.setPropertyValue(kv.getKey(), kv.getValue());
		}
	}

	public Sale findById(Integer id) {
		return sleRepo.findById(id);
	}
	
	public Sale findByReference(String reference) {
		return sleRepo.findByReference(reference);
	}

	public int count() {
		return sleRepo.count();
	}

	public List<Sale> list(int page, int pageSize) {
		return sleRepo.list(page, pageSize);
	}
	
	public boolean delete(Sale sale) {
		List<Journal> journals = jnlRepo.list(sale);
		for (Journal journal: journals) {
			jnlRepo.delete(journal);
		}
		return sleRepo.delete(sale);
	}
	
	public boolean update(Sale sale, Map<String, ?> params) {
		return sleRepo.update(sale, params);
	}
	
	@PreDestroy
	public void onDestroy() {
	}
}
