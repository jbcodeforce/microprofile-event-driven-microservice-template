package ibm.gse.eda.app.domain;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ibm.gse.eda.app.infrastructure.events.OrderEvent;
import ibm.gse.eda.app.infrastructure.events.OrderPayload;
import ibm.gse.eda.app.infrastructure.kafka.OrderEventProducer;

@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	private OrderEventProducer eventProducer;
	
	public OrderService() {
		eventProducer = new OrderEventProducer();
	}
	
	public OrderService(OrderEventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}
	
	public void createOrder(OrderEntity order) {
		OrderEvent orderEvent = new OrderEvent(System.currentTimeMillis(),
				OrderEvent.TYPE_ORDER_CREATED);
		OrderPayload orderPayload = new OrderPayload(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
				order.getDeliveryAddress());
		orderEvent.setPayload(orderPayload);
		try {
			logger.severe("emit event for " + order.getOrderID());
			eventProducer.emit(orderEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
