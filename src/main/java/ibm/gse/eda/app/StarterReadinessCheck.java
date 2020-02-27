package ibm.gse.eda.app;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import ibm.gse.eda.app.infrastructure.kafka.OrderEventsAgent;

@Readiness
@ApplicationScoped
public class StarterReadinessCheck implements HealthCheck {

	@Inject
	OrderEventsAgent orderEventsAgent;
	
	/**
	 * Validate kafka consumers are ready
	 * @return
	 */
    public boolean isReady() {
        // perform readiness checks, e.g. database connection, etc.
    	boolean evalIt = orderEventsAgent.isRunning();
        return evalIt;
    }
	
    @Override
    public HealthCheckResponse call() {
        boolean up = isReady();
        return HealthCheckResponse.named(this.getClass().getSimpleName()).state(up).build();
    }
    
}
