import React, { useState, useReducer, createContext, useRef } from 'react';
import { useForm } from 'react-hook-form';

//app context
export const AppContext = createContext();
//app provider
export const AppProvider = ({ children }) => {

	const growler = useRef(undefined);
	const checkoutForm = useForm();
	const card = useRef();
	const [cartKey] = useState('cart');
	const [expressCustomerKey] = useState('express.customer');
	const [expressShippingKey] = useState('express.shipping');
	const [expressPhoneKey] = useState('express.phone');
	const [svc] = useState({
		forms: {
			formatCurrency(value) {
				return value.toLocaleString('en-US', {
					//style: 'currency', 
					//currency: 'Kes',
					maximumFractionDigits: 2, 
					minimumFractionDigits: 0
				});
			},
			formatDateTime(value) {
				const d = new Date(value);
				return d.toLocaleDateString('en-US', {year: 'numeric', month: 'short', day: 'numeric'});
			},
			onTextChange(e, field, obj) {
				obj[field.name] = e.target.value;
				field.onChange(e);
			},
			onToggle(e, field, obj) {
				obj[field.name] = e.checked;
				field.onChange(e);
			},
			onSelectOption(e, field, obj) {
				obj[field.name] = e.value;
				field.onChange(e);
			}
		}
	});

	const getCart = () => {
		let obj = localStorage.getItem(cartKey);
		if (!obj) {
			return undefined;           
		}
		return JSON.parse(obj);
	}

	const setCart = (obj) => {
		if (!obj) {
			localStorage.removeItem(cartKey);
		} else {
			localStorage.setItem(cartKey, JSON.stringify(obj));
		} 
	}

	const [cart, cartHandler] = useReducer((state, action) => { 
		switch (action.type) {
			case 'set':
				setCart(action.cart);
			break;
			case 'reset':
				setCart(undefined);
			break;
			default:
				//do nothing
		}
		return getCart();
	}, getCart());
	
	const setExpressCustomer = (obj) => {
		if (!obj) {
			localStorage.removeItem(expressCustomerKey);
		} else {
			localStorage.setItem(expressCustomerKey, JSON.stringify(obj));
		} 
	}

	const getExpressCustomer = () => {
		let obj = localStorage.getItem(expressCustomerKey);
		if (!obj) {
			return undefined;
		}
		return JSON.parse(obj);
	}
	
	const setExpressShipping = (obj) => {
		if (!obj) {
			localStorage.removeItem(expressShippingKey);
		} else {
			localStorage.setItem(expressShippingKey, JSON.stringify(obj));
		} 
	}

	const getExpressShipping = () => {
		let obj = localStorage.getItem(expressShippingKey);
		if (!obj) {
			return undefined;
		}
		return JSON.parse(obj);
	}
	
	const setExpressPhone = (obj) => {
		if (!obj) {
			localStorage.removeItem(expressPhoneKey);
		} else {
			localStorage.setItem(expressPhoneKey, JSON.stringify(obj));
		} 
	}

	const getExpressPhone = () => {
		let obj = localStorage.getItem(expressPhoneKey);
		if (!obj) {
			return undefined;
		}
		return JSON.parse(obj);
	}
    
	const [express, expressHandler] = useReducer((state, action) => {
		let current = {...state};
		switch(action.type) {
			case 'customer':
				current.customer = {...current.customer, ...action.data};
				setExpressCustomer(current.customer);
			break;
			case 'shipping':
				current.shipping = {...current.shipping, ...action.data};
				setExpressShipping(current.shipping);
			break;
			case 'phone':
				current.phone = {...current.phone, ...action.data};
				setExpressPhone(current.phone);
			break;
			case 'customer.clear':
				setExpressCustomer(undefined);
			break;
			case 'shipping.clear':
				setExpressShipping(undefined);
			break;
			case 'phone.clear':
				setExpressPhone(undefined);
			break;
			default:
				//do nothing
		}
		return {
			customer: getExpressCustomer(),
			shipping: getExpressShipping(),
			phone: getExpressPhone()
		};
	}, {
		customer: getExpressCustomer(),
		shipping: getExpressShipping(),
		phone: getExpressPhone()
	}); 

	const getGlobals = () => {
		let obj = localStorage.getItem('app.data');
		if (!obj) { 
		    return {
			genders: {},
			countries: []
		    };           
		}
		return JSON.parse(obj);
	}

	const setGlobals = (obj) => {
		const key = 'app.data';
		if (!obj) {
			localStorage.removeItem(key);
		} else {
			localStorage.setItem(key, JSON.stringify(obj));
		} 
	}

	const [globals, globalsHandler] = useReducer((state, action) => {
		let current = {...state};
		switch (action.type) {
			case 'genders':
				current.genders = {...current.genders, ...action.data};
			break;
			case 'countries':
				current.countries = [...current.countries, ...action.data];
			break;
			default:
				//do nothing
		}
		setGlobals(current);
		return getGlobals();
	}, getGlobals()); 

	return (
		<AppContext.Provider value={{growler, globals, globalsHandler, express, expressHandler, cart, cartHandler, checkoutForm, card, svc}}>
			{children}
		</AppContext.Provider>
	);
};
/*
//cart context
export const CartContext = createContext();
//cart provider
export const CartProvider = ({children}) => { 
        
	const checkoutForm = useForm();
	const card = useRef();
	const [cartKey] = useState('cart');

	const getCart = () => {
		let obj = localStorage.getItem(cartKey);
		if (!obj) {
			return undefined;           
		}
		return JSON.parse(obj);
	}

	const setCart = (obj) => {
		if (!obj) {
			localStorage.removeItem(cartKey);
		} else {
			localStorage.setItem(cartKey, JSON.stringify(obj));
		} 
	}

	const [cart, cartHandler] = useReducer((state, action) => { 
		switch (action.type) {
			case 'set':
				setCart(action.cart);
			break;
			case 'reset':
				setCart(undefined);
			break;
			default:
				//do nothing
		}
		return getCart();
	}, getCart()); 

	return (
		<CartContext.Provider value={{cart, cartHandler, checkoutForm, card}}>
			{children}
		</CartContext.Provider>
	);
};

//catalog context
export const CatalogContext = createContext();
//catalog provider
export const CatalogProvider = ({children}) => {
    
	const [sort] = useState([
	{
		label: 'Price Asc',
		sortBy: {
			field: 'price',
			order: 1
		},
		icon: 'pi pi-sort-asc'
	},
	{
		label: 'Price Desc',
		sortBy: {
			field: 'price',
			order: -1
		},
		icon: 'pi pi-sort-desc'
	}]);
	const [catalogKey] = useState('catalog.filter');

	const setCatalog = (obj) => {
		if (!obj) {
			localStorage.removeItem(catalogKey);
		} else {
			localStorage.setItem(catalogKey, JSON.stringify(obj));
		}
	}

	const getCatalog = () => {
		let obj = localStorage.getItem(catalogKey);
		if (!obj) {
			return {
				page: {
					offset: 0,
					limit: 10
				},
				filter: {
					name: '',
					categories: [],
					types: [],
					brands: [],
					conditions: [],
					price: null,
					rating: null,
					sortBy: null
				}
			};
		}
		return JSON.parse(obj);
	}

	const [catalog, catalogHandler] = useReducer((state, action) => {
		switch(action.type) {
			case 'clear':
				state = undefined;
			break;
			case 'filter':
				state.filter = {...state.filter, ...action.data};
			break;
			case 'page':
				state.page = {...state, ...action.data};
			break
			default:
			return state;
		}
		setCatalog(state);
		return getCatalog();
	}, getCatalog());

	return (
		<CatalogContext.Provider value={{catalog, catalogHandler, sort}}>
			{children}
		</CatalogContext.Provider>
	);
};

export const OrderContext = createContext();

export const OrderProvider = ({children}) => {

    const [key] = useState('orders.filter');
    const {user} = useContext(AppContext);

    const setParams = (obj) => {
        if (!obj) {
            localStorage.removeItem(key);
        } else {
            localStorage.setItem(key, JSON.stringify(obj));
        }        
    }

    const getParams = () => {
        let obj = localStorage.getItem(key);
        if (!obj) {
            return {
                page: {
                    offset: 0,
                    limit: 10
                },
                filter: {
                    buyers: [user.login.profile],
                    from: null,
                    to: null,
                    types: null,
                    states: [], 
                    reference: '',
                    sortBy: null
                }
            };
        }
        return JSON.parse(obj);
    }

    const [orders, ordersHandler] = useReducer((state, action) => {
        switch(action.type) {
            case 'page':
                state.page = {...state, page: action.data} 
            break;
            case 'filter':
                state.filter = {...state.filter, ...action.data}
            break;
            case 'sortBy':
                state.filter = {...state.filter, ...action.data}
            break;
            default:
                return state;
        }
        setParams(state);
        return getParams();
    }, getParams());

    return (
        <OrderContext.Provider value={{orders, ordersHandler}}>
            {children}
        </OrderContext.Provider>
    );
}

export const ReceiptContext = createContext();

export const ReceiptProvider = ({ children }) => {

    const [key] = useState('receipts.filter');
    const {user} = useContext(AppContext);

    const setParams = (obj) => {
        if (!obj) {
            localStorage.removeItem(key);
        } else {
            localStorage.setItem(key, JSON.stringify(obj));
        }        
    }

    const getParams = () => {
        let obj = localStorage.getItem(key);
        if (!obj) {
            return {
                page: {
                    offset: 0,
                    limit: 10
                },
                filter: {
                    reference: '',
                    payers: [user.login.profile],
                    created: {
                        from: null,
                        to: null
                    },
                    amount: {
                        min: null,
                        max: null
                    },
                    types: [],
                    sales: [],
                    sortBy: null
                }
            }
        }
        return JSON.parse(obj);
    }

    const [receipts, receiptHandler] = useReducer((state, action) => {
        switch(action.type) {
            case 'page':
                state.page = {...state, page: action.data} 
            break;
            case 'filter':
                state.filter = {...state.filter, ...action.data}
            break;
            case 'sortBy':
                state.filter = {...state.filter, ...action.data}
            break;
            default:
                return state;
        }
        setParams(state);
        return getParams();
    }, getParams());

    return (
        <ReceiptContext.Provider value={{receipts, receiptHandler}}>
            {children}
        </ReceiptContext.Provider>
    );
}
*/
