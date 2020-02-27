package ibm.gse.eda.app.infrastructure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ibm.gse.eda.app.infrastructure.kafka.KafkaConfiguration;
import ibm.gse.eda.app.infrastructure.kafka.OrderEventsAgent;


/**
 * Servlet context listener to start the different messaging consumers of 
 * the application. As each consumer acts as an agent, continuously listening to
 * events, we need to start them when the encapsulating app / microservice is successfuly
 * started, which is why we have to implement a servlet context listener.
 *  
 *  It uses one agent to consume from the order topic
 *  When the application stops we need to close safely the consumers.
 *
 */
@WebListener
public class AgentsInitializer implements ServletContextListener{
    private static final Logger logger = Logger.getLogger(AgentsInitializer.class.getName());

    @Inject
    private OrderEventsAgent orderEventsAgent;
    
    private ExecutorService executor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("@@@ Microservice contextInitialized v0.0.1, start agents");
        executor = Executors.newFixedThreadPool(1);
        executor.execute(orderEventsAgent);
    }

   
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info(" context destroyed");
        orderEventsAgent.stop();
        executor.shutdownNow();
        try {
            executor.awaitTermination(KafkaConfiguration.TERMINATION_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            logger.warning("awaitTermination( interrupted " + ie.getLocalizedMessage());
        }
    }

}