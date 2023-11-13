//import './app.css';
import React, { useState, useRef, useEffect, useContext } from 'react';
import { Link, Routes, Route } from 'react-router-dom';
import { Toast } from 'primereact/toast';
import { Controller, useForm } from 'react-hook-form';
import { AutoComplete } from 'primereact/autocomplete';
import { ConfirmDialog } from 'primereact/confirmdialog';
import { Image } from 'primereact/image';
import { BlockUI } from 'primereact/blockui';
import { Button } from 'primereact/button';
import { useCheckout } from './views/hooks';
import { AppContext } from './context/CartProvider';
import { Cart, CheckoutSuccess } from './views/cart';

const Logo = () => {

	return (
		<div className="align-self-stretch flex align-items-center justify-content-center px-2 md:px-5 bg-gray-800">
			<Link to="/shop" className="font-bold text-lg no-underline text-color">
				<div className="app-logo-text">
					<span>E</span><span>-</span><span>Store</span>
			      	</div>
		      	</Link>
		</div>
	);
}

const Search = () => {

	const searchFormRef = useRef();
	const searchForm = useForm();
	const {handleSubmit, control, reset} = searchForm;

	const doSearch = (e) => { 
		reset(e);
	}

	return (
		<form ref={searchFormRef} onSubmit={handleSubmit(doSearch)} className="w-full">
			<div className="flex flex-1 px-3">
				<span className="flex align-items-center">
					<i className="pi pi-search text-gray-500"></i>
				</span>
				<Controller name="name" control={control} defaultValue={''} rules={{}} render={({field}) => (				
					<AutoComplete id={field.name} {...field} value={field.value} dropdownMode={false} suggestions={[]} delay={3000} onChange={(e) => field.onChange(e)} className="w-full flex-grow-1 outline-none border-none" inputClassName="search-box appearance-none outline-none border-none flex-auto bg-transparent outline-none p-component text-gray-500 pl-3" placeholder="Search" />
				)} />	
			</div>
		</form>
	);
}

const Header = () => {
	
	const navMenu = useRef(null);
	const {growler} = useContext(AppContext);
	const {cart} = useCheckout();	
	
	const toggle = (e) => {
		navMenu.current.classList.toggle('hidden');
	}
	
	return (
		<header className="align-self-start sticky h-auto z-1 top-0 w-full">
			<Toast ref={growler} position="top-right" />  
			<ConfirmDialog />  
			<nav className="bg-gray-900 flex align-items-center relative p-fluid">
				<Logo />
				<Search />
				<div onClick={toggle} className="p-ripple cursor-pointer flex align-items-center justify-content-center md:hidden text-white pr-3">
					<i className="pi pi-bars text-4xl"></i>
					<span role="presentation" className="p-ink" style={{height: '84px', width: '84px', top: '3px', left: '-16px'}}></span>
				</div>
				<div ref={navMenu} className="hidden md:flex absolute md:static left-0 top-100 z-1 shadow-2 md:shadow-none w-full md:w-auto bg-gray-900">
					<ul className="list-none p-0 m-0 flex select-none flex-column md:flex-row md:flex-row-reverse border-top-1 border-gray-800 md:border-top-none">
						<li>
							<Link to="/cart" className="p-ripple h-full flex pl-3 pr-6 py-3 md:px-3 md:py-2 align-items-center text-gray-500 hover:text-gray-400 hover:bg-gray-800 font-medium cursor-pointer transition-colors transition-duration-150 no-underline">
								<i className="pi pi-shopping-cart p-overlay-badge text-base md:text-2xl mr-2 lg:mr-0">
									{cart.size() > 0 ? <span className="p-badge p-component p-badge-dot p-badge-success"></span> : <></>}
								</i>
								<span className="block md:hidden md:font-medium">Cart</span>
								<span role="presentation" className="p-ink" style={{height: '84px', width: '84px', top: '-1px', left: '-20px'}}></span>
							</Link>
						</li>				
					</ul>
				</div>
			</nav>
		</header>
	);
}

const Catalog = () => { 
    
	const [count, setCount] = useState(0);
	const [items, setItems] = useState([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		setLoading(true); 
		const count = async () => {
			const resp = await fetch('/api/catalog/count');
			if (resp.ok) {
				return await resp.json();
			}
			return 0;
		}    
		const list = async (offset, limit) => {
			const resp = await fetch(`/api/catalog/list/${offset}/${limit}`);
			if (resp.ok) {
				return await resp.json();
			}
			return [];
		}
		(async () => {
			let cnt;
			let lst;            
			try {
				cnt = await count();
				if (cnt === 0) {
					lst = [];
				} else {
					lst = await list(0, cnt);
				}  
			} catch (err) {
				cnt = 0;
				lst = [];
			} 
			setCount(cnt);
			setItems(lst);
			setLoading(false);
		})();
	}, []);

	if (count) {
	}

	return (
		<BlockUI blocked={loading} template={<span><i className="pi pi-spin pi-spinner font-bold text-200 text-4xl"/></span>} containerClassName="h-auto overflow-auto flex-grow-1">    
			<div className="grid grid-nogutter w-full z-0">
				{items.map((item, index) => <CatalogItem key={index} item={item} />)}  
			</div>
		</BlockUI>
	);
}

const CatalogItem = ({item}) => {
	
	const {cart} = useCheckout();

	const addToCart = (e) => {
		cart.add(item, 1);
	}

	return (
		<div className="col-12 xs:col-6 sm:col-6 md:col-4 xl:col-3 p-2 relative">
			<div className="surface-card shadow-1 border-round-sm text-center">
				<Image src={`/api/catalog/image/${item.id}`} />
				<div className="relative p-4 text-center">
					<Button label="Add to cart" icon="pi pi-cart-plus" onClick={addToCart} rounded>
					</Button>
					<span title={item.name} className="mt-3 text-sm block text-overflow-ellipsis white-space-nowrap overflow-hidden text-600">{item.name}</span>
				</div>
			</div>
		</div>
	);
}

function useInit() {
	const {globals, globalsHandler} = useContext(AppContext);
	const {cart, customer, shipping, phone} = useCheckout();
	const [application] = useState((function() {
		//application closure
		return {
			async init() {
				if (!globals.genders || Object.entries(globals.genders).length === 0) {
					const resp = await fetch('/api/customer/genders');
					if (resp.ok){
						globalsHandler({type: 'genders', data: await resp.json()});
					}
				}
				if (!globals.countries || globals.countries.length === 0) {
					const resp = await fetch('/api/phone/codes');
					if (resp.ok){
						globalsHandler({type: 'countries', data: await resp.json()});
					}
				}
				//initialize cart
				if (!cart.instance) {
					await cart.create();
				}
				//initialize customer
				if (!customer.instance) {
					await customer.create();
				}
				//initialize shipping
				if (!shipping.instance) {
					await shipping.create();
				}
				//initialize phone
				if (!phone.instance) {
					await phone.create();
				}
			}
		};
	})());
	
	return application;
}

function App() {

	const [started, setStarted] = useState(false);
	const application = useInit();

	useEffect(() => {	
		(async () => {
			await application.init();
			setStarted(true);
		})();
		return () => {
			//unregister interceptor here
		} 
	}, [application]); 

	if (!started) {
		return (<div className="m-auto pi-spin font-bold text-2xl text-green-400">Loading...</div>);
	}
	
	return (
		<div className="h-full overflow-hidden flex flex-column justify-content-start">
			<Header />
			<main className="h-full flex flex-column overflow-hidden z-0">
				<Routes>
					<Route index element={<Catalog />} />
					<Route path="/shop" element={<Catalog />} />
					<Route path="/cart/*" element={
						<Routes>
							<Route index element={<Cart />} />
							<Route path="/checkout-success" element={<CheckoutSuccess />} />
						</Routes>
					} />
				</Routes>
			</main>
		</div>
	);
}

export default App;
