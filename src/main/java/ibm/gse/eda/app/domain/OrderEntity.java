package ibm.gse.eda.app.domain;

import ibm.gse.eda.app.infrastructure.events.Address;

public class OrderEntity {
	public static final String PENDING_STATUS = "pending";
    public static final String CANCELLED_STATUS = "cancelled";
    public static final String ASSIGNED_STATUS = "assigned";
    public static final String BOOKED_STATUS = "booked";
    public static final String REJECTED_STATUS = "rejected";
    public static final String COMPLETED_STATUS = "completed";
    
    private String orderID;
    private String productID;
    private String customerID;
    private int quantity;
    private Address deliveryAddress;
    private String status;
    
	public OrderEntity(String orderID, String productID, String customerID, int quantity, Address deliveryAddress,
			String status) {
		super();
		this.orderID = orderID;
		this.productID = productID;
		this.customerID = customerID;
		this.quantity = quantity;
		this.deliveryAddress = deliveryAddress;
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrderID() {
		return orderID;
	}
	public String getProductID() {
		return productID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public int getQuantity() {
		return quantity;
	}
	public Address getDeliveryAddress() {
		return deliveryAddress;
	}
    
    
}
