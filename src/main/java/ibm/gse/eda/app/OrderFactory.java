package ibm.gse.eda.app;

import java.util.UUID;

import ibm.gse.eda.app.domain.OrderEntity;
import ibm.gse.eda.app.dto.OrderParameters;

public class OrderFactory {

	public static OrderEntity createNewOrder(OrderParameters dto) {
		OrderEntity order = new OrderEntity(UUID.randomUUID().toString(),
                dto.getProductID(),
                dto.getCustomerID(),
                dto.getQuantity(),
                dto.getDestinationAddress(),
                OrderEntity.PENDING_STATUS);
	   return order;
	}
}
