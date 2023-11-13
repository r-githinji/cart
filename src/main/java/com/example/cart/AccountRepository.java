package com.example.cart;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
	
	private EntityManager em;
	@Autowired
	public AccountRepository(EntityManager em) {
		this.em = em;
	}
    
	public Account find(String number) { 
		Query cq = em.createNamedQuery("accQuery", Account.class)
			.setParameter(1, number);
		return (Account)cq.getResultList().stream().findFirst().orElse(null);
	}
	
	public boolean update(Account account, Map<String, ?> params) {
		if (null == params || params.isEmpty()) {
			return false;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Account> cu = cb.createCriteriaUpdate(Account.class);
		Root<Account> acc = cu.from(Account.class);
		for (Map.Entry<String, ?> tuple: params.entrySet()) {
			cu = cu.set(acc.get(tuple.getKey()), tuple.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(acc.get("number"), account.getNumber())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}

	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Account> acc = cq.from(Account.class);
		cq = cq.select(cb.count(acc)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> list(int offset, int limit) {
		Query cq = em.createNamedQuery("accQuery", Account.class)
			.setParameter(1, null);
		return cq.setFirstResult(offset).setMaxResults(limit).getResultList();
	}
} 
