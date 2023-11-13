package com.example.cart;

import java.util.List;
import java.util.LinkedList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JournalRepository {

	private EntityManager em;
	@Autowired
	public JournalRepository(EntityManager em) {
		this.em = em;
	}

	public Journal create(Journal journal) {
		return em.merge(journal);
	}
	
	public List<Journal> list(Sale sale) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Journal> cq = cb.createQuery(Journal.class);
		Root<Journal> jnl = cq.from(Journal.class);
		Predicate[] preds = new Predicate[]{cb.equal(jnl.get("document"), sale.getReference())};
		cq = cq.select(jnl).where(preds);
		return em.createQuery(cq).getResultList();
	}
	
	public boolean delete(Journal journal) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Entry> cd = cb.createCriteriaDelete(Entry.class);
		Root<Entry> entry = cd.from(Entry.class);
		//delete entries
		Predicate[] preds = new Predicate[] {cb.equal(entry.get("journal"), journal)};		
		cd = cd.where(preds);
		em.createQuery(cd).executeUpdate();
		//delete journal
		CriteriaDelete<Journal> cd0 = cb.createCriteriaDelete(Journal.class);
		Root<Journal> jnl = cd0.from(Journal.class);
		preds = new Predicate[] {cb.equal(jnl.get("id"), journal.getId())};
		cd0 = cd0.where(preds);
		return em.createQuery(cd0).executeUpdate() > 0;
	}
}
