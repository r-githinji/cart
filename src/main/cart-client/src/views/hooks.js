import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../context/CartProvider';

export function usePhone() {
	
	const {express, expressHandler, globals} = useContext(AppContext);
	return (function() {
		return {
			instance: express.phone,
			codes() {
				const {countries} = globals;
				return (countries || []);
			},
			async create() {
				try {
					const resp = await fetch('/api/phone/create');
					if (resp.ok) {
						const data = await resp.json();
						this.init(data);
					} else {
						//handle created customer failed
					}
				} catch (err) {
					console.log(err.message);
				}
			},
			store() {
				expressHandler({type: 'phone', data: this.instance});
			},
			init(obj) {
				this.instance = obj;
				this.store();
			},
			clear() {
				expressHandler({type: 'phone.clear'});
			}
		};
	})();
}

export function useCustomer() {
	
	const {express, expressHandler, globals} = useContext(AppContext);
	return (function() {
		return {
			instance: express.customer,
			phone: {
				add(record) {
					let map = this.book();
					map[this.format(record)] = record;
					this.instance.phones = Object.values(map);
				}, 
				remove(record) {
					let map = this.book();
					delete map[this.format(record)];
					this.instance.phones = Object.values(map);
				}, 
				clear(record) {
					this.instance.phones = [];  
				},
				book() {
					if (!this.instance.phones) {
						this.instance.phones = [];
					}
					return this.instance.phones.reduce((map, record) => {
						map[this.format(record)] = record;
						return map;
					}, {});					
				},
				format(record) {
					return `${record.area.code}${record.msisdn}`;
				},
				get(msisdn) {
					let map = this.book();
					return map[msisdn];
				}
			},
			store() {
				expressHandler({type: 'customer', data: this.instance});
			},
			init(obj) {
				this.instance = obj;
				this.store();
			},
			clear() {
				expressHandler({type: 'customer.clear'});
			},
			async create() {
				try {
					const resp = await fetch('/api/customer/create');
					if (resp.ok) {
						const data = await resp.json();
						this.init(data);
					} else {
						//handle created customer failed
					}
				} catch (err) {
					console.log(err.message);
				}
			},
			genders() {
				const {genders} = globals;
				return (Object.entries(genders) || []).map(kv => {
					return {
						label: kv[1],
						value: kv[0]
					};
				});
			}
		};
	})();
}

export function useShipping() {
	
	const {express, expressHandler} = useContext(AppContext); 
	return (function() {	
		return {
			instance: express.shipping,
			store() {
				expressHandler({type: 'shipping', data: this.instance});
			},
			init(obj) {
				this.instance = obj;
				this.store();
			},
			async create() {
				try {
					const resp = await fetch('/api/address/create');
					if (resp.ok) {
						//shipping address
						const data = await resp.json();
						this.init(data);
					} else {
						//handle create shipping error
					}
					
				} catch (err) {
					console.log(err.message);
				}
			},
			clear() {
				expressHandler({type: 'shipping.clear'});
			}
		};
	})();
}

//shopping cart object
export function useCart() {
	
	const {growler, cart, cartHandler} = useContext(AppContext);
	return (function() {
		return {
			instance: cart,
			lines() {
				if (!this.instance.lines) {
					this.instance.lines = [];
				}
				return (this.instance.lines || []).reduce((map, line) => {
					map[line.item.id] = line;
					return map;
				}, {});
			},
			size() {
				return (this.instance && this.instance.lines && this.instance.lines.length) || 0;
			},
			add(itm, qty) {
				let gross = (qty * itm.sellPrice).toFixed(2) * 1;
				let tax = (gross * .16).toFixed(2) * 1;//vat
				let nett = (gross - tax).toFixed(2) * 1;
				let line = {
					id: null,
					quantity: qty,
					item: itm,
					cost: itm.sellPrice,
					gross: gross,
					nett: nett,
					created: [new Date().toJSON().split('.')[0], 'Z'].join(''),
					tax: tax    
				};
				let items = this.lines();
				items[line.item.id] = line;
				this.instance.lines = Object.values(items);           
				this.aggregate();
				this.store();
				growler.current.show({
					severity: 'info', 
					detail: `${line.item.name} added to cart!`
				});
			},
			remove(line) { 
				let items = this.lines();    
				delete items[line.item.id];
				this.instance.lines = Object.values(items);    
				this.aggregate();
				this.store();
				growler.current.show({
					severity: 'info', 
					detail: `${line.item.name} removed!`
				});
			},
			clear() {
				this.instance.lines = [];    
				this.aggregate(); 
				this.store();
				growler.current.show({
					severity: 'info', 
					detail: 'cart cleared'
				});
			},
			aggregate() {
				this.instance.nett = this.instance.lines.reduce((sum, line) => sum + line.nett, 0.0).toFixed(2) * 1; 
				this.instance.tax = this.instance.lines.reduce((sum, line) => sum + line.tax, 0.0).toFixed(2) * 1;
				this.instance.gross = (this.instance.nett + this.instance.tax).toFixed(2) * 1;
				this.instance.amountInCents = (this.instance.gross * 10000).toFixed(2) / 100;
			},
			store() {				
				cartHandler({
					type: 'set', 
					cart: this.instance
				});
			},
			init(obj) {
				this.instance = obj;
				this.store();
			},
			async create() {
				try {
					const resp = await fetch('/api/cart/create');
					if (resp.ok) {
						const data = await resp.json();
						this.init(data);
					} else {
						throw Error('unable to create cart!');
					}
				} catch (err) {
					console.log(err.message);
				}
			},
			checkQty(item) {
				let qty;
				if (item) {
					const basket = this.lines();
					const line = basket[item.id];
					if (line) {
						qty = line.quantity;
					}
				} 
				if (qty === undefined) {
					qty = 1;
				}
				return qty;
			}
		};
	})();
}

export function useCheckout() {
	
	const {growler} = useContext(AppContext);
	const navigate = useNavigate();
	const cart = useCart();
	const customer = useCustomer();
	const shipping = useShipping(); 
	const phone = usePhone(); 
	//checkout closure
	const checkout = (function() {
		return {
			cart: cart,
			customer: customer,
			shipping: shipping,
			phone: phone,
			async pay(e) {
				//store phone
				phone.store();
				//store customer
				customer.store();
				//store shipping
				shipping.store();
				//add phone
				customer.instance.phones = [phone.instance];
				//populate cart instance
				cart.instance.debtor = customer.instance;
				cart.instance.shipTo = shipping.instance;
				cart.instance.postedOn = [new Date().toJSON().split('.')[0], 'Z'].join('');
				//proceed to make payment
				try {
					const resp = await fetch('/api/cart/checkout', {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json'
						},
						body: JSON.stringify(cart.instance)
					});
					switch (resp.status) {
						case 201:						
							const data = await resp.json();
							cart.init(data);
							//navigate to confirm page
							navigate('/cart/checkout-success');
						break;
						default:
							//handle checkout failed scenario
							growler.current.show(await resp.json());
					} 
				} catch (err) {
					growler.current.show({
						detail: err.message,
						severity: 'error'
					});
				}
			}
		};
	})();
	
	return checkout;
}
