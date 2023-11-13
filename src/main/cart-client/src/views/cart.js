import { useContext, useState, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { AutoComplete } from 'primereact/autocomplete';
import { Button } from 'primereact/button';
import { DataView } from 'primereact/dataview';
import { confirmDialog } from 'primereact/confirmdialog';
import { classNames } from 'primereact/utils';
import { useForm, Controller } from 'react-hook-form';
import { Tooltip } from 'primereact/tooltip';
import { Checkbox } from 'primereact/checkbox';
import { Image } from 'primereact/image';
import { Dropdown } from 'primereact/dropdown';
import { useCheckout } from './hooks';
import { AppContext } from '../context/CartProvider';

// discount/coupon code
export const Coupon = () => {

	const couponForm = useForm();
	const {control, reset, handleSubmit} = couponForm;

	const apply = async (form) => {
		//implement coupon functionality here
		reset();
	}

	return (
		<form onSubmit={handleSubmit(apply)}>
			<div className="flex flex-column row-gap-2">
				<div className="p-inputgroup relative">
					<Controller name="code" control={control} defaultValue={''} rules={{required: '*enter discount code'}} render={({field, fieldState}) => (
						<InputText id={field.name} value={field.value} {...field} onChange={(e) => field.onChange(e)} placeholder="coupon code" className={[classNames(fieldState.error ? 'p-invalid' : '')].join(' ')} />
					)} />
					<Button label="APPLY" size="small" className="text-xs p-inputgroup-addon-button" />
				</div>
			</div>
		</form>
	);
}

//my cart
export const Cart = () => {

	const navigate = useNavigate();
	const {checkoutForm} = useContext(AppContext);
	const {control, handleSubmit} = checkoutForm;
	const checkout = useCheckout();
	const {cart} = checkout;
	
	const doCheckout = async (e) => {
		checkout.pay(e);
	}

	if (cart.size() === 0) {
		return (
			<EmptyCart />
		);
	}

	return (
		<div className="overflow-auto h-full">
			<DataView className="cart" layout={'list'} value={cart.instance.lines} itemTemplate={(line) => <CartItem line={line} />} header={<CartHeader />} footer={<CartFooter />} gutter={false} />
			<form onSubmit={handleSubmit(doCheckout)}>
				<div className="card p-2">
					<h3 className="text-600 px-2">
						<i className="pi pi-check-circle mr-2" />
						<span>Cart Checkout</span>
					</h3>
					<div className="p-fluid">
						<div className="card">	
							<h4 className="text-500 px-2 mb-5">Billing Details</h4>
					    	    	<BillTo />
				    	    	</div>
				    	    	<div className="card mt-6">
					    	    	<h4 className="text-500 px-2 mb-5">Shipping Address</h4>
					    	    	<ShipTo />
				    	    	</div>
				    	    	<div className="card mt-5">
					    	    	<h4 className="text-500 px-2 mb-5">Credit Card</h4>
					    	    	<CreditCard />
					    	</div>
				    	    	<div className="p-2 border-1 surface-border border-round surface-hover mt-2">
							<div className="my-3">
								<Controller name="newsletter" control={control} defaultValue={false} rules={{}} render={({field, fieldState}) => (
									<div className="inline-flex flex-row gap-2 align-items-center">
										<Checkbox inputId={field.name} {...field} value={field.value} checked={field.value} onChange={(e) => field.onChange(e)} />
										<span className="text-sm text-400">
											I would like to receive exclusive emails with discounts and product information
										</span>
									</div>
								)} />
							</div>
							<p className="line-height-3 text-sm text-600">
								Your personal data will be used to process your order, support your experience throughout this website, and for other purposes described in our privacy policy.
							</p>
						</div>
			    	    	</div>
				</div>				
				<div className="px-2 pb-2 inline-flex flex-row gap-2">
					<Button label="CHECKOUT NOW" severity="success" icon="pi pi-check text-normal" raised iconPos="right" className="border-noround text-xs" />
					<Button type="button" label="BACK TO SHOP" onClick={(e) => navigate('/shop')} icon="pi pi-shopping-bag text-normal" raised text className="border-noround text-color text-xs surface-hover"  />
				</div>
			</form>		
		</div>
	);
}

//cart item
const CartItem = ({line}) => {

	const {svc} = useContext(AppContext);
	const cartItemForm = useForm();
	const {control, handleSubmit} = cartItemForm;
	const cartItemFormRef = useRef(); 
	const {cart} = useCheckout();

	const remove = (e) => { 
		cart.remove(line);
	}

	const update = (e) => {
		cart.add(line.item, e.quantity);
	} 

	const onChange = (e, field) => {
		field.onChange(e.value); 
		cartItemFormRef.current.dispatchEvent(new Event('submit', {cancelable: true, bubbles: true}));     
	}
	
	return (
		<form ref={cartItemFormRef} onSubmit={handleSubmit(update)} className="flex-auto p-input-filled col-12">
			<div className="flex flex-row flex-wrap gap-3 align-items-start sm:align-items-center">
				<div className="w-6rem sm:w-8rem text-center">		    	
					<Image src={`/api/catalog/image/${line.item.id}`} width="64" height="64" />
				</div>

				<div className="flex-auto px-3">
					<div className="flex align-items-center justify-content-between mb-2">
						<span className="text-900 font-medium">{line.item.name}</span>
						<span className="text-900 font-bold">${svc.forms.formatCurrency(line.gross)}</span>
					</div>

					<div className="text-600 text-sm mb-2">Green | Small</div>

					<div className="flex flex-auto justify-content-between align-items-center">
						<Controller name="quantity" control={control} defaultValue={line.quantity} rules={{required: '*quantity required'}} render={({field, fieldState}) => (							
							<InputNumber inputId={field.name} value={field.value} {...field} min={1} size={1} onChange={(e) => onChange(e, field)} showButtons buttonLayout="horizontal" 
								inputClassName={classNames('text-center border-none', {'p-invalid': fieldState.error})} incrementButtonClassName="p-button-text border-1 surface-border" decrementButtonClassName="p-button-text border-1 surface-border"
								incrementButtonIcon="pi pi-plus text-color" decrementButtonIcon="pi pi-minus text-color" />
						)} />
						<Button type="button" onClick={remove} icon="pi pi-trash text-red-500" link rounded text />
					</div>
				</div>
			</div>
		</form>
	);
}

// empty cart
export const EmptyCart = () =>{

	return (
		<div className="flex flex-column flex-nowrap w-full h-full justify-content-center align-items-center row-gap-4 overflow-hidden">
			<Link to="/shop">
				<i className="pi pi-shopping-cart text-7xl shadow-1 p-7 bg-green-50 border-circle cursor-pointer p-text-secondary" />
			</Link>
			<span className="font-bold text-2xl text-yellow-500">Your cart is empty!</span>
		</div>
	);
}

// cart header
const CartHeader = () => {

	const {cart} = useCheckout();

	const clear = (e) => {
		confirmDialog({
			message: 'Do you want to proceed and clear cart?',
			header: 'Confirm',
			icon: 'pi pi-exclamation-triangle',
			accept: () => {
				cart.clear();
			},
			reject: () => {
				//do nothing
			}
		});
	}

	return (
		<div className="flex flex-row flex-nowrap justify-content-between align-items-center p-2 border-bottom-1 border-round-top surface-border p-text-secondary">
			<div className="flex flex-row flex-nowrap column-gap-2 align-items-center pl-2">
				<i className="pi pi-shopping-cart text-2xl" />
				<span className="text-lg font-medium text-700">Your Cart</span>
			</div>
			<div className="hidden flex-row flex-nowrap gap-2 align-items-center">	
				<span className="text-sm">{cart.lines.length}&nbsp;ITEM{cart.instance.lines.length === 1 ? '': 'S'}</span>            
				<Tooltip target=".clear-cart" className="text-xs" />
				<Button disabled={cart.instance.lines.length === 0} icon="pi pi-trash" link size="small" text rounded onClick={clear} className="p-sidebar-icon clear-cart" data-pr-tooltip="Clear Cart" data-pr-position="left" />
			</div>
		</div>
	);
}

//cart footer
export const CartFooter = () => {
    
	const {svc} = useContext(AppContext);
	const {cart} = useCheckout();

	return (
		<div className="grid grid-nogutter text-sm p-3 row-gap-2 align-items-center">
			<div className="col-12 p-0 flex flex-row flex-nowrap justify-content-between text-600 font-medium">
				<span className="text-sm">Subtotal</span>
				<span>{svc.forms.formatCurrency(cart.instance.nett)}</span>
			</div>
			<div className="col-12 p-0 flex flex-row flex-nowrap justify-content-between text-600 font-medium">
				<span className="text-sm">Tax</span>
				<span>{svc.forms.formatCurrency(cart.instance.tax)}</span>
			</div>  
			<div className="col-12 p-0 flex flex-row flex-nowrap justify-content-between text-600 font-medium">
				<span className="text-sm">Shipping</span>
				<span>{svc.forms.formatCurrency(cart.instance.shipping)}</span>
			</div>    
			<div className="col-12 p-0 pt-3 flex flex-row flex-nowrap justify-content-between text-600 font-medium text-lg mt-2 border-top-1 surface-border">
				<span>Total</span>
				<span>{svc.forms.formatCurrency(cart.instance.gross)}</span>
			</div> 
		</div> 
	);
}

//mini cart
export const MiniCart = () => {

	const {svc} = useContext(AppContext);
	const {cart} = useCheckout();

	const cartItem = (rowData) => {
		return (
			<div className="p-2 flex flex-row flex-nowrap align-items-center col-12">
				<span className="col-2 text-center inline-flex align-items-center gap-1">
					<i className="pi pi-times text-xs" />{rowData.quantity}
				</span>
				<span className="col-9 text-overflow-ellipsis white-space-nowrap overflow-hidden">{rowData.item.product.name}</span>                
				<span className="col text-right">{svc.forms.formatCurrency(rowData.total)}</span>
			</div>
		);
	}

	return (
		<DataView className="mini-cart" layout={'list'} value={cart.instance.lines} itemTemplate={cartItem} header={<CartHeader />} footer={<CartFooter />} gutter={false} />
	);
}

// billing info
export const BillTo = () => {

	const {svc, checkoutForm} = useContext(AppContext);
	const {formState: {errors}, control} = checkoutForm;
	const {customer} = useCheckout();	
	
	return (
		<div className="grid grid-nogutter gap-5">
    	    		<div className="col">
    	    			<Controller name={'firstName'} control={control} defaultValue={customer.instance.firstName || ''} rules={{required: '*firstname required'}} render={({field, fieldState}) => (
	    	    			<span className="p-float-label">
	    	    				<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, customer.instance)} />
	    	    				<ErrorOrLabel path={field.name} errors={errors} label="First name" />
	    	    			</span>
	    	    		)} />
    	    		</div>
    	    		<div className="col">
    	    			<Controller name={'lastName'} control={control} defaultValue={customer.instance.lastName || ''} rules={{required: '*lastname required'}} render={({field, fieldState}) => (
	    	    			<span className="p-float-label">
	    	    				<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, customer.instance)} />
	    	    				<ErrorOrLabel path={field.name} errors={errors} label="Last name" />
	    	    			</span>
	    	    		)} />
    	    		</div>
    	    		<div className="col-12">
    	    			<Controller name={'email'} control={control} defaultValue={customer.instance.email || ''} rules={{required: '*email required'}} render={({field, fieldState}) => (
	    	    			<span className="p-float-label">
	    	    				<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, customer.instance)} />
	    	    				<ErrorOrLabel path={field.name} errors={errors} label="Email Address" />
	    	    			</span>
	    	    		)} />
    	    		</div>
    	    		<div className="col-12">
    	    			<Controller name={'gender'} control={control} defaultValue={customer.instance.gender} rules={{required: '*gender required'}} render={({field, fieldState}) => (
	    	    			<span className="p-float-label">			    	    				
	    	    				<Dropdown inputId={field.name} {...field} value={field.value} options={customer.genders()} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, customer.instance)} />
	    	    				<ErrorOrLabel path={field.name} errors={errors} label="Gender" />
	    	    			</span>
	    	    		)} />
    	    		</div>
    	    		<Phone />
    	    	</div>
	);
}

export const ShipTo = () => {

	const {svc, checkoutForm} = useContext(AppContext);
	const {control, formState: {errors}} = checkoutForm;
	const {shipping} = useCheckout();

	return (
		<div className="p-fluid">
			<div className="grid grid-nogutter gap-5">
				<div className="col-12">
					<Controller name={'street'} control={control} defaultValue={shipping.instance.street || ''} rules={{required: '*street required'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, shipping.instance)} />
							<ErrorOrLabel path={field.name} errors={errors} label="Street" />
						</span>
					)} />
				</div>
				<div className="md:col-7 col-12">
					<Controller name={'avenue'} control={control} defaultValue={shipping.instance.avenue || ''} rules={{required: '*avenue required'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, shipping.instance)} />
							<ErrorOrLabel path={field.name} errors={errors} label="Avenue" />
						</span>
					)} />
				</div>
				<div className="col">
					<Controller name={'houseNo'} control={control} defaultValue={shipping.instance.houseNo || ''} rules={{required: '*hse or apt required'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, shipping.instance)} />
							<ErrorOrLabel path={field.name} errors={errors} label="House/Apart" />
						</span>
					)} />
				</div>
				<div className="col-12">
					<Controller name={'city'} control={control} defaultValue={shipping.instance.city || ''} rules={{required: '*city or town required'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, shipping.instance)} />
							<ErrorOrLabel path={field.name} errors={errors} label="City or Town" />
						</span>
					)} />
				</div>
			</div>
			<div className="mt-4 px-2">
				<Controller name={'remember'} defaultValue={shipping.instance.remember || false} control={control} rules={{}} render={({field, fieldState}) => (
					<span className="inline-flex flex-row align-items-center gap-2">
						<Checkbox inputId={field.name} {...field} checked={field.value} onChange={(e) => svc.forms.onToggle(e, field, shipping.instance)} />
						<label htmlFor={field.name} className="text-500 font-medium text-sm">
							<span>Remember this address?</span>
						</label>
					</span>
				)} />
			</div>
		</div>
	);
}

export const Phone = () => {
	
	const {svc, checkoutForm} = useContext(AppContext);
	const {control, formState: {errors}} = checkoutForm;
	const {phone} = useCheckout();
	const [suggestions, setSuggestions] = useState([]);
	
	const completeArea = (e) => {
		setSuggestions(phone.codes().filter(elem => elem.code.startsWith(e.query)));
	}
	
	return (
		<div className="col-12 flex">
			<Controller name={'area'} defaultValue={phone.instance.area} control={control} rules={{required: '*ext required'}} render={({field, fieldState}) => (
				<span className="p-float-label md:w-3 w-4">
					<AutoComplete inputId={field.name} {...field} value={field.value} field="code" forceSelection suggestions={suggestions} completeMethod={completeArea} inputClassName="border-noround-right border-right-none" className={classNames({'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onSelectOption(e, field, phone.instance)}/>
					<ErrorOrLabel path={field.name} errors={errors} label="Ext." />
				</span>
			)} />
			<Controller name={'msisdn'} defaultValue={phone.instance.msisdn || ''} control={control} rules={{required: '*msisdn required', pattern: {value: /^([0-9]{9})$/, message: '*Enter valid msisdn'}}} render={({field, fieldState}) => (
				<span className="p-float-label md:w-9 w-8">
					<InputText id={field.name} {...field} value={field.value} className={classNames('border-noround-left', {'p-invalid': fieldState.error})} onChange={(e) => svc.forms.onTextChange(e, field, phone.instance)} />
					<ErrorOrLabel path={field.name} errors={errors} label="Msisdn" />
				</span>
			)} />
		</div>
	);
}

export const CreditCard = () => {
	
	const {checkoutForm} = useContext(AppContext);
	const {control, formState: {errors}} = checkoutForm;
	
	return (
		<div className="grid grid-nogutter gap-5 p-fluid">
			<div className="col-12">
				<Controller name={'cardNumber'} defaultValue={''} control={control} rules={{required: '*card number required'}} render={({field, fieldState}) => (
					<span className="p-float-label">
						<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => field.onChange(e)} />
						<ErrorOrLabel path={field.name} errors={errors} label="Card number" />
					</span>
				)} />
			</div>
			<div className="col">
				<div className="flex flex-row flex-nowrap">
					<Controller name={'month'} defaultValue={undefined} control={control} rules={{required: '*exp month'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputNumber inputId={field.name} {...field} value={field.value} min={1} max={12} inputClassName="border-noround-right border-right-none" className={classNames({'p-invalid': fieldState.error})} onChange={(e) => field.onChange(e.value)} />
							<ErrorOrLabel path={field.name} errors={errors} label="Month" />
						</span>
					)} />
					<Controller name={'year'} defaultValue={undefined} control={control} rules={{required: '*exp year'}} render={({field, fieldState}) => (
						<span className="p-float-label">
							<InputNumber inputId={field.name} {...field} value={field.value} inputClassName="border-noround-left" className={classNames({'p-invalid': fieldState.error})} onChange={(e) => field.onChange(e.value)} />
							<ErrorOrLabel path={field.name} errors={errors} label="Year" />
						</span>
					)} />
				</div>
			</div>
			<div className="md:col-8 col-12">
				<Controller name={'cvv'} defaultValue={''} control={control} rules={{required: '*cvv required'}} render={({field, fieldState}) => (
					<span className="p-float-label">
						<InputText id={field.name} {...field} value={field.value} className={classNames({'p-invalid': fieldState.error})} onChange={(e) => field.onChange(e)} />
						<ErrorOrLabel path={field.name} errors={errors} label="Cvv" />
					</span>
				)} />
			</div>
		</div>
	);
}

// checkout success
export const CheckoutSuccess = () => {

	const {svc, checkoutForm} = useContext(AppContext);
	const navigate = useNavigate();
	const {cart, customer, shipping, phone} = useCheckout();
	const {reset} = checkoutForm;

	const complete = async (e) => {
		navigate('/shop');
		reset();
		//initialize cart
		await cart.create();
		//initialize express customer
		await customer.create();
		//initialize express shipping
		await shipping.create();
		//initalize phone
		await phone.create();
	}

	return (
		<div className="card border-round-xl h-auto w-5 m-auto shadow-2 text-align-center p-5 flex flex-column align-items-center">
			<span className="block text-center">
				<i className="pi pi-check-circle text-600 font-bold text-3xl"></i>
			</span>
			<h3 className="font-medium text-700">Thank You, {cart.instance.debtor.name}</h3> 
			<p>We acknowledge your payment of KES {svc.forms.formatCurrency(cart.instance.gross)}</p>
			<span className="block mt-5 mb-4 text-lg font-medium text-600">Order {cart.instance.reference} placed!</span>
			<div className="my-4 text-center">
				<Button text raised rounded label="COMPLETE" onClick={complete} className="text-color" />
			</div>
			<div>
				<ul className="list-none m-0 p-0 flex flex-row flex-nowrap align-items-center justify-content-center gap-1">
					<li>Copyright {new Date().getFullYear()}</li>
					<li>|</li>
					<li>All rights reserved</li>
				</ul>
			</div>
		</div>
	);
}

export const ErrorOrLabel = ({path, errors, label, inline}) => {
    
	const error = path.split('.').reduce((obj, key) => {
		if (obj) {
			return obj[key];
		}
		return undefined;
	}, errors);

	return (
		<label htmlFor={path}>
			<span role="alert" className={classNames('text-sm', {'text-red-500 zoomin animation-duration-1000': error})}>{error ? error.message : label}</span>
		</label>
	);
}
