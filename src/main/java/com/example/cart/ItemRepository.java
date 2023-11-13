package com.example.cart;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional @Repository
public class ItemRepository {

	private EntityManager em;
	@Autowired
	public ItemRepository(EntityManager em) {
		this.em = em;
	}
	
	public Item create(Item item) {
		return em.merge(item);
	}
	
	public Item findById(Integer id) {
		return em.find(Item.class, id);
	}
	
	public boolean update(Item item, Map<String, ?> params) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Item> cu = cb.createCriteriaUpdate(Item.class);
		Root<Item> itm = cu.from(Item.class);
		for (Map.Entry<String, ?> entry: params.entrySet()) {
			cu = cu.set(itm.get(entry.getKey()), entry.getValue());
		}
		Predicate[] preds = new Predicate[] {cb.equal(itm.get("id"), item.getId())};
		cu = cu.where(preds);
		return em.createQuery(cu).executeUpdate() > 0;
	}
	
	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Item> itm = cq.from(Item.class);
		cq = cq.select(cb.count(itm)).where();
		return em.createQuery(cq).getSingleResult().intValue();
	}
		
	public List<Item> list(int offset, int limit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> itm = cq.from(Item.class);
		cq = cq.select(itm).where();
		return em.createQuery(cq).setFirstResult(offset).setMaxResults(limit).getResultList();
	}
    
	public boolean delete(Item item) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Item> cd = cb.createCriteriaDelete(Item.class);
		Root<Item> itm = cd.from(Item.class);
		Predicate[] preds = new Predicate[] {cb.equal(itm.get("id"), item.getId())};
		cd = cd.where(preds);
		return em.createQuery(cd).executeUpdate() > 0;
	}
}
