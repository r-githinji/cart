package com.example.cart;

import java.util.Map;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepository {
	
	private EntityManager em;
	@Autowired
	public CountryRepository(EntityManager em) {
		this.em = em;
	}
	
	public Country create(Country country) {
		return em.merge(country);
	}
	
	public boolean update(Country country, Map<String, ?> params) {
		if (null == params || params.isEmpty()) {
			return false;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Country> cu = cb.createCriteriaUpdate(Country.class);
		Root<Country> ctry = cu.from(Country.class);
		for (Map.Entry<String, ?> tuple: params.entrySet()) {
			cu = cu.set(ctry.get(tuple.getKey()), tuple.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(ctry.get("id"), country.getId())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}	

	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Country> acc = cq.from(Country.class);
		cq = cq.select(cb.count(acc)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<Country> list(int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(Country.class);
		Root<Country> ctry = cq.from(Country.class);
		cq = cq.select(ctry).where();
		return em.createQuery(cq).setFirstResult(offset).setMaxResults(limit).getResultList();
	}
	
	public boolean delete(Country country) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Country> cd = cb.createCriteriaDelete(Country.class);
		Root<Country> ctry = cd.from(Country.class);
		Predicate[] preds = new Predicate[] {cb.equal(ctry.get("id"), country.getId())};
		cd = cd.where(preds);
		return em.createQuery(cd).executeUpdate() > 0;
	}
}

