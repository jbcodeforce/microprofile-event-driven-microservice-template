package ut.app;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ibm.gse.eda.app.domain.OrderEntity;
import ibm.gse.eda.app.domain.OrderService;
import ibm.gse.eda.app.infrastructure.events.OrderEvent;

public class TestOrderCreation {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void shouldGenerateOrderCreatedEventAfterOrderCreatedAtServiceLevel() {
		
		OrderEventEmitterMock evtProducer = new OrderEventEmitterMock();
		OrderService orderService = new OrderService(evtProducer);
		OrderEntity order = OrderTestDataFactory.orderFixtureWithoutIdentity();
		orderService.createOrder(order);
		
		Assert.assertTrue(evtProducer.eventEmitted);
		OrderEvent createOrderEvent = evtProducer.getEventEmitted();
		Assert.assertNotNull(createOrderEvent);
		Assert.assertTrue(OrderEvent.TYPE_ORDER_CREATED.equals(createOrderEvent.getType()));
	}

}
