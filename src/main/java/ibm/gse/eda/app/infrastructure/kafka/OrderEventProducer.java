package ibm.gse.eda.app.infrastructure.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import ibm.gse.eda.app.infrastructure.events.EventEmitter;
import ibm.gse.eda.app.infrastructure.events.OrderEvent;

@ApplicationScoped
public class OrderEventProducer implements EventEmitter {
	 private static final Logger logger = Logger.getLogger(OrderEventProducer.class.getName());

		@Inject
		private KafkaConfiguration kafkaConfiguration;
		
		private Jsonb jsonb = JsonbBuilder.create();
	    private KafkaProducer<String, String> kafkaProducer;


	    public OrderEventProducer() {
	        init();
	    }
	    
	    protected void init() {
	    	Properties properties = getKafkaConfiguration().getProducerProperties("order-event-producer");
	        kafkaProducer = new KafkaProducer<String, String>(properties);
	    	
	    }
	    
	    public KafkaConfiguration getKafkaConfiguration() {
	    	// to bypass some strange NPE, as inject does not seem to work
	    	if (kafkaConfiguration == null ) {
	    		kafkaConfiguration = new KafkaConfiguration();
	    	}
	    	return kafkaConfiguration;
	    }
	    
	    @Override
	    @Retry(retryOn=TimeoutException.class,
	    maxRetries = 4,
        maxDuration = 10000,
        delay = 200,
        jitter = 100,
        abortOn=InterruptedException.class)
	    @Timeout(KafkaConfiguration.PRODUCER_TIMEOUT_SECS * 1000)
	    public void emit(OrderEvent orderEvent) throws Exception  {
	    	String value = jsonb.toJson(orderEvent);
	    	logger.info("Send " + value);
	    	String key = orderEvent.getPayload().getOrderID();
	        ProducerRecord<String, String> record = new ProducerRecord<>(getKafkaConfiguration().getOrdersTopicName(), key, value);

	        Future<RecordMetadata> send = kafkaProducer.send(record, 
	        		new Callback() {

						@Override
						public void onCompletion(RecordMetadata metadata, Exception exception) {
							if (exception != null) {
								exception.printStackTrace();
							} else {
							   System.out.println("The offset of the record just sent is: " + metadata.offset());
				                   
							}
						}
	        		}
	        );
	        try {
				send.get(KafkaConfiguration.PRODUCER_TIMEOUT_SECS, TimeUnit.SECONDS);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	    }

		@Override
		public void safeClose() {
			kafkaProducer.close();
		}
}
