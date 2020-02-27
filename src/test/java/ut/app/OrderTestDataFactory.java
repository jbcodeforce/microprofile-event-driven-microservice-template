package ut.app;

import ibm.gse.eda.app.domain.OrderEntity;
import ibm.gse.eda.app.infrastructure.events.Address;

public class OrderTestDataFactory {

	public static OrderEntity orderFixtureWithoutIdentity() {
		
		Address destinationAddress = new Address("Street", "City", "DestinationCountry", "State", "Zipcode");
		OrderEntity order = new OrderEntity("","P01","AFarmer",100, 
				destinationAddress,  OrderEntity.PENDING_STATUS);
		return order;
	}
}
