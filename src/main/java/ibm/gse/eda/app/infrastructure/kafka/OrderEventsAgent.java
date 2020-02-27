package ibm.gse.eda.app.infrastructure.kafka;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;

import ibm.gse.eda.app.infrastructure.events.OrderEvent;

/**
 * Base runnable agent to continuously listen to event on the main topic 
 *
 */
@ApplicationScoped
public class OrderEventsAgent implements Runnable {
	private static final Logger logger = Logger.getLogger(OrderEventsAgent.class.getName());

	private KafkaConsumer<String, String> kafkaConsumer = null;
	private Jsonb jsonb = JsonbBuilder.create();
	private boolean running = true;
	
	@Inject
	private KafkaConfiguration kafkaConfiguration;
	
	public OrderEventsAgent() {
    }
    
    public OrderEventsAgent(KafkaConsumer<String, String> kafkaConsumer) {
    	this.kafkaConsumer = kafkaConsumer;
    }
    
    public boolean isRunning() {
    	return running;
    }
    
    private void init() {
    	// if we need to have multiple threads then the clientId needs to be different
    	// auto commit is set to true, and read from the last not committed offset
    	Properties properties = kafkaConfiguration.getConsumerProperties(
          		"OrderEventsAgent",	
          		true,  
          		"latest" );
        this.kafkaConsumer = new KafkaConsumer<String, String>(properties);
    	this.kafkaConsumer.subscribe(Collections.singletonList(kafkaConfiguration.getOrdersTopicName()));
    }
    
	@Override
	public void run() {
		init();
		while (this.running) {
			try {
				Queue<OrderEvent> events = poll();
				for (OrderEvent event : events) {
					handle(event);
				}
			} catch (KafkaException  ke) {
				ke.printStackTrace();
				// when issue on getting data from topic we need to reconnect
				stop();
			}
		}
		stop();
	}

	public void stop() {
		this.running = false;
		try {
			if (kafkaConsumer != null)
				kafkaConsumer.close(KafkaConfiguration.CONSUMER_CLOSE_TIMEOUT);
        } catch (Exception e) {
            logger.warning("Failed closing Consumer " +  e.getMessage());
        }
	}

	private Queue<OrderEvent> poll(){
		 ConsumerRecords<String, String> recs = kafkaConsumer.poll(KafkaConfiguration.CONSUMER_POLL_TIMEOUT);
	     Queue<OrderEvent> result = new LinkedList<OrderEvent>();
	        for (ConsumerRecord<String, String> rec : recs) {
	        	OrderEvent event = deserialize(rec.value());
	            result.add(event);
	        }
	        return result;
	}
	
	private OrderEvent deserialize(String eventAsString) {
		return jsonb.fromJson(eventAsString, OrderEvent.class);
	}
	
	private void handle(OrderEvent event) {
		logger.info("Event to process with business logic: " + event.getPayload().getOrderID());
	}
}
