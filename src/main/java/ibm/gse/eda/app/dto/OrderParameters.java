package ibm.gse.eda.app.dto;

import ibm.gse.eda.app.infrastructure.events.Address;

public class OrderParameters {
	 private String customerID;
	 private String productID;
	 private int quantity;
	 private Address destinationAddress;
	
	
	public OrderParameters() {
		// needed for jaxrs serialization
	}
	 
	public OrderParameters(String customerID, String productID, int quantity, Address destinationAddress) {
		super();
		this.customerID = customerID;
		this.productID = productID;
		this.quantity = quantity;
		this.destinationAddress = destinationAddress;
	}
	
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Address getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(Address destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
}
