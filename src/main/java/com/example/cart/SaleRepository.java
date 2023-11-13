package com.example.cart;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

import jakarta.persistence.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaUpdate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class SaleRepository {

	private EntityManager em;
	@Autowired
	public SaleRepository(EntityManager em) {
		this.em = em;
	}
	
	public String series() {
		StringBuilder sb = new StringBuilder()
			.append(" SELECT CONCAT('SLE-', LPAD(NEXTVAL('sle_ref_seq'), 6, '0'), '-', EXTRACT(YEAR FROM NOW())) ");
		Query q = em.createNativeQuery(sb.toString());
		return (String)q.getSingleResult();
	}
	
	public Sale create(Sale sale) {
		return em.merge(sale);
	}
	
	public boolean update(Sale sale, Map<String, ?> params) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Sale> cu = cb.createCriteriaUpdate(Sale.class);
		Root<Sale> sle = cu.from(Sale.class);
		Predicate[] preds = new Predicate[] {cb.equal(sle.get("id"), sale.getId())};
		for (Map.Entry<String, ?> entry: params.entrySet()) {
			cu = cu.set(sle.get(entry.getKey()), entry.getValue());
		}
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}

	public Sale findById(Integer id) {
		return em.find(Sale.class, id);
	}
	
	public Sale findByReference(String reference) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sale> cq = cb.createQuery(Sale.class);
		Root<Sale> sle = cq.from(Sale.class);
		Predicate[] preds = new Predicate[] {cb.equal(sle.get("reference"), reference)};
		cq = cq.select(sle).where(preds);
		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Sale> sle = cq.from(Sale.class);
		cq = cq.select(cb.count(sle)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<Sale> list(int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sale> cq = cb.createQuery(Sale.class);
		Root<Sale> sle = cq.from(Sale.class);
		cq = cq.select(sle).where();
		return em.createQuery(cq).getResultList();
	}
    
	public boolean delete(Sale sale) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<SaleLine> cd = cb.createCriteriaDelete(SaleLine.class);
		Root<SaleLine> ln = cd.from(SaleLine.class);
		Predicate[] preds = new Predicate[] {cb.equal(ln.get("sale"), sale)};
		cd = cd.where(preds);
		em.createQuery(cd).executeUpdate();
		//delete sale oject
		CriteriaDelete<Sale> cd0 = cb.createCriteriaDelete(Sale.class);
		Root<Sale> sle = cd0.from(Sale.class);	
		preds = new Predicate[] {cb.equal(sle.get("id"), sale.getId())};		
		cd0 = cd0.where(preds);
		return em.createQuery(cd0).executeUpdate() > 0;
	}
}
