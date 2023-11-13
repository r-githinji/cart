/*insert chart of accounts*/
with cte as (
	select a.* 
		from (values('1001', 'Cash', 0, 'ASSET'), 
	                  ('1002', 'Accounts Payable', 0, 'LIABILITY'), 
	                  ('1003', 'Inventory', 0, 'ASSET'), 
	                  ('1004', 'Cost Of Goods Sold', 0, 'EXPENSE'), 
	                  ('1005', 'Accounts Receivable', 0, 'ASSET'), 
	                  ('1006', 'Capital', 0, 'EQUITY'), 
	                  ('1007', 'Sales', 0, 'REVENUE'), 
	                  ('1009', 'Unallocated Payment', 0, 'LIABILITY'), 
	                  ('1010', 'Insurance Expense', 0, 'EXPENSE'), 
	                  ('1011', 'Insurance Payable', 0, 'LIABILITY'), 
	                  ('1012', 'Interest Expense', 0, 'EXPENSE'), 
	                  ('1013', 'Interest Payable', 0, 'LIABILITY'), 
	                  ('1014', 'Interest Income', 0, 'REVENUE'), 
	                  ('1015', 'Interest Receivable', 0, 'ASSET'), 
	                  ('1016', 'Gain', 0, 'GAIN'),
	                  ('1017', 'Loss', 0, 'LOSS'),
	                  ('1018', 'Sales Tax', 0, 'LIABILITY'),
	                  ('1019', 'Purchase Tax', 0, 'ASSET'),
	                  ('1020', 'Withholding Tax', 0, 'LIABILITY'),
	                  ('1021', 'Loans Payable', 0, 'LIABILITY'),
	                  ('1022', 'Deposit Trust Account', 0, 'ASSET'),
	                  ('1023', 'Prepaid Revenue', 0, 'REVENUE')
		) a (no, descr, bal, type)
	where true
)
insert into acc (acc_no, acc_created, acc_desc, acc_bal, acc_type)
select a.no, NOW(), a.descr, a.bal, a.type 
	from cte a left join acc b on b.acc_no = a.no
where b.acc_no is null;

/*insert countries*/
with cte as (
	select v.*
		from (values ('254', 'Kenya'),
			 ('255', 'Tanzania'),
			 ('256', 'Uganda')
	) v (code, name)
	where true
)
insert into ctry (ctry_code, ctry_name)
select v.code, v.name
	from cte v left join ctry x on x.ctry_code = v.code
where x.ctry_code is null;

/*insert items*/
with cte as (
	select a.* 
		from (values('Playstation 4 1TB', 4.67, 354, 5, 'NEW'),
			('Sony Bravia Smart Tv', 7.77, 536, 2, 'NEW'),
			('Samsung Smart Watch', 5.09, 76, 7, 'NEW'),
			('Macbook Pro Laptop', 7.74, 689, 3, 'NEW'),
			('Tecno Smartphone', 5.39, 389, 8, 'USED')
		) a (name, markup, price, restock, condition) 
	where true
)
insert into itm (itm_name, itm_created, itm_markup, itm_price, itm_ret_price, itm_rsv, itm_cond)
select v.name, NOW(), v.markup, v.price, round(v.price + (v.price * v.markup / 100.0), 2), v.restock, v.condition 
	from cte v 
where true;












