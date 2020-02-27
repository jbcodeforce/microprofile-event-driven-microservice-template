package ibm.gse.eda.app.infrastructure.kafka;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KafkaConfiguration {
	private static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());
	public static final long PRODUCER_TIMEOUT_SECS = 10;
	public static final long PRODUCER_CLOSE_TIMEOUT_SEC = 10;
	public static final Duration CONSUMER_POLL_TIMEOUT = Duration.ofSeconds(10);
	public static final Duration CONSUMER_CLOSE_TIMEOUT = Duration.ofSeconds(10);
    public static final long TERMINATION_TIMEOUT_SEC = 10;

  
    @Inject 
    @ConfigProperty(name = "ordersTopicName")
    protected String ordersTopicName;
   
    @Inject 
    @ConfigProperty(name = "kafka-acknowledge")
    protected String acknowledge;
    
    @Inject 
    @ConfigProperty(name = "kafka-producer-idempotence")
    protected boolean idempotence;
    
    @Inject 
    @ConfigProperty(name = "kafka-consumer-groupid")
    protected String groupid;
    
    
	private String clientId;
    
    public KafkaConfiguration() {}
    
    
    public Properties getConsumerProperties(
    		String clientid, 
    		boolean commit,
    		String offset) {
        Properties properties = buildCommonProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,  getConsumerGroupID());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.toString(commit));
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, getClientId());
        properties.forEach((k,v)  -> logger.info(k + " : " + v)); 
        return properties;
    }
    
    /**
     * Take into account the environment variables if set
     *
     * @return common kafka properties
     */
    private  Properties buildCommonProperties() {
        Properties properties = new Properties();
        Map<String, String> env = System.getenv();

        if (env.get("KAFKA_BROKERS") == null) {
            throw new IllegalStateException("Missing environment variable KAFKA_BROKERS");
        }
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, env.get("KAFKA_BROKERS"));

    	if (env.get("KAFKA_APIKEY") != null) {
          properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
          properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
          properties.put(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\""
                            + env.get("KAFKA_APIKEY") + "\";");
          properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
          properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
          properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");

          if ("true".equals(env.get("TRUSTSTORE_ENABLED"))){
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env.get("TRUSTSTORE_PATH"));
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env.get("TRUSTSTORE_PWD"));
          }
        }
        return properties;
    }

	public String getOrdersTopicName() {
		
		if (ordersTopicName == null) {
			ordersTopicName = "orders";
		}
		return ordersTopicName;
	}
	
	public String getAcknowledge() {
		if (acknowledge == null) {
			acknowledge = "1";
		}
		return acknowledge;
	}
	
	public String getConsumerGroupID() {
		if (groupid == null) {
			groupid = "order-ms-consumer-grp";
		}
		return groupid;
	}
	
	public boolean getIdempotence() {
		return idempotence;
	}

	public String getClientId() {
		if (clientId == null) {
			clientId="OrderEventsAgent";
		}
		
		return clientId;
	}

	public Properties getProducerProperties(String clientId) {
		Properties properties = buildCommonProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.ACKS_CONFIG, getAcknowledge());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, getIdempotence());
        properties.forEach((k,v)  -> logger.info(k + " : " + v)); 
        return properties;
	}

}
