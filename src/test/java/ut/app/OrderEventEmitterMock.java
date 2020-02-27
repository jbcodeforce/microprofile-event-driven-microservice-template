package ut.app;

import ibm.gse.eda.app.infrastructure.events.OrderEvent;
import ibm.gse.eda.app.infrastructure.kafka.OrderEventProducer;

/**
 * Use this mockup class to emit order events
 *
 */
public class OrderEventEmitterMock extends OrderEventProducer  {
	
	public OrderEventEmitterMock() {}
	
	public boolean eventEmitted = false;
	public OrderEvent emittedEvent = null;

	@Override
	public void emit(OrderEvent event) throws Exception {
		this.eventEmitted = true;
		this.emittedEvent = event;	
	}
	
	@Override
	protected void init() {}

	@Override
	public void safeClose() {
		this.eventEmitted = false;
		this.emittedEvent = null;	
	}

	public OrderEvent getEventEmitted() {
		return emittedEvent;
	}
	
}
