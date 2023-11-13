package com.example.cart;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AddressRepository {

	private EntityManager em;
	@Autowired
	public AddressRepository(EntityManager em) {
		this.em = em;
	}
	
	public Address findById(Integer id) {
		return em.find(Address.class, id);
	}
	
	public Address create(Address address) {
		return em.merge(address);
	}
	
	public boolean update(Address address, Map<String, ?> params) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Address> cu = cb.createCriteriaUpdate(Address.class);
		Root<Address> add = cu.from(Address.class);
		for (Map.Entry<String, ?> entry: params.entrySet()) {
			cu = cu.set(add.get(entry.getKey()), entry.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(add.get("id"), address.getId())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}
	
	public boolean delete(Address address) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Address> cd = cb.createCriteriaDelete(Address.class);
		Root<Address> add = cd.from(Address.class);
		Predicate[] preds = new Predicate[] {cb.equal(add.get("id"), address.getId())};
		cd = cd.where(preds);
		return em.createQuery(cd).executeUpdate() > 0;
	}
}
