/*customer reference sequence*/
create sequence if not exists cus_ref_seq;
/*customer table(debtors e.t.c)*/
create table if not exists cus
(
	cus_id serial,
	cus_ref text not null unique,
	cus_fname text not null,
	cus_lname text not null,
	cus_gender text not null,
	cus_email text not null,
	cus_created timestamp not null,
	cus_is_active boolean,
	primary key(cus_id),
	unique(cus_ref)
);
/*address table*/
create table if not exists addr 
(
	addr_id serial,
	addr_seq integer not null,
	addr_street text not null,
	addr_avenue text not null,
	addr_hseno text not null,
	addr_city text not null,
	addr_remember boolean not null,
	addr_cus integer,
	primary key(addr_id),
	unique(addr_seq, addr_cus),
	foreign key(addr_cus) references cus(cus_id)
);
/*country table(phone codes)*/
create table if not exists ctry 
(
	ctry_id serial,
	ctry_code text not null,
	ctry_name text not null,
	primary key (ctry_id),
	unique(ctry_code)
);
/*phone table*/
create table if not exists phone 
(
	ph_id serial,
	ph_no text,
	ph_code integer,
	ph_cus integer,
	primary key(ph_id),
	foreign key (ph_code) references ctry(ctry_id),
	foreign key (ph_cus) references cus(cus_id),
	unique(ph_no, ph_code)
);
/*item datastructure*/
create table if not exists itm
(
    	itm_id serial,
    	itm_name text not null,
    	itm_created timestamp not null,
    	itm_rsv integer not null,
    	itm_markup double precision not null,
    	itm_price double precision not null,
    	itm_ret_price double precision,
    	itm_cond text,
    	itm_rtng double precision,
    	primary key(itm_id),
    	unique (itm_name)
);
/*journal datastructure*/
create table if not exists jnl 
(
	jnl_id serial,
	jnl_type text not null,
	jnl_created timestamp not null,
	jnl_doc text,
	primary key (jnl_id),
	unique (jnl_doc, jnl_type)
);
/*accounts table*/
create table if not exists acc 
(
	acc_no text not null,
	acc_desc text not null,
	acc_bal double precision not null,
	acc_created timestamp not null,
	acc_type text not null,
	primary key (acc_no)
);
/*entry datastructure*/
create table if not exists entry 
(
	entry_id serial,
	entry_seq integer not null,
	entry_type text not null,
	entry_acc text,
	entry_amt double precision,
	entry_jnl integer,
	entry_created timestamp,
	primary key (entry_id),
	unique (entry_jnl, entry_seq),
	foreign key (entry_acc) references acc(acc_no),
	foreign key (entry_jnl) references jnl (jnl_id)
);
/*sale reference sequence*/
create sequence if not exists sle_ref_seq;
/*sale table*/
create table if not exists sle 
(
	sle_id serial,
	sle_ref text not null,
	sle_tax double precision not null,
	sle_shipping double precision not null,
	sle_gross double precision not null,
	sle_nett double precision not null,
	sle_created timestamp not null,
	sle_postedon timestamp,
	sle_status text not null,
	sle_type text not null,
	sle_isdirect boolean not null,	
	sle_debtor integer,
	sle_shipto integer,
	primary key(sle_id),
	unique(sle_ref, sle_type),
	foreign key (sle_debtor) references cus (cus_id),
	foreign key (sle_shipto) references addr (addr_id)
);
/*sale line table*/
create table if not exists sle_ln 
(
	ln_id serial,
	ln_sle int,
	ln_itm int,
	ln_qty int not null,
	ln_created timestamp not null,
	ln_cost double precision not null,
	ln_tax double precision not null,
	ln_gross double precision not null,
	ln_nett double precision not null,
	primary key (ln_id),
	foreign key (ln_sle) references sle (sle_id),
	foreign key (ln_itm) references itm (itm_id)
);
