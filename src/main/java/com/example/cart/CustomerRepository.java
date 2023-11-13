package com.example.cart;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;

@Repository
public class CustomerRepository {

	private EntityManager em;
	@Autowired
	public CustomerRepository(EntityManager em) {
		this.em = em;
	}
	
	public String series() {
		StringBuilder sb = new StringBuilder()
			.append(" SELECT CONCAT('CUS-', LPAD(NEXTVAL('cus_ref_seq'), 6, '0'), '-', EXTRACT(YEAR FROM NOW())) ");
		Query q = em.createNativeQuery(sb.toString());
		return (String)q.getSingleResult();
	}
		
	public Customer create(Customer customer) {		
		return em.merge(customer);
	}
	
	public Customer findById(Integer id) {
		return em.find(Customer.class, id);
	}
	
	public Customer findByReference(String reference) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
		Root<Customer> cus = cq.from(Customer.class);
		Predicate[] preds = new Predicate[] {cb.equal(cus.get("reference"), reference)};
		cq = cq.select(cus).where(preds);
		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}
	
	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Customer> cus = cq.from(Customer.class);
		cq = cq.select(cb.count(cus)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	public List<Customer> list(int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
		Root<Customer> cus = cq.from(Customer.class);
		cq = cq.select(cus).where();
		return em.createQuery(cq).setFirstResult(offset).setMaxResults(limit).getResultList();
	} 
	
	public boolean delete(Customer customer) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Customer> cd = cb.createCriteriaDelete(Customer.class);
		Root<Customer> cus = cd.from(Customer.class);
		Predicate[] preds = new Predicate[] {cb.equal(cus.get("id"), customer.getId())};
		cd = cd.where(preds);
		return em.createQuery(cd).executeUpdate() > 0;
	}
	
	public boolean update(Customer customer, Map<String, ?> params) {
		if (null == params || params.isEmpty()) {
			return false;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Customer> cu = cb.createCriteriaUpdate(Customer.class);
		Root<Customer> cus = cu.from(Customer.class);
		for (Map.Entry<String, ?> tuple: params.entrySet()) {
			cu = cu.set(cus.get(tuple.getKey()), tuple.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(cus.get("id"), customer.getId())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}
}
