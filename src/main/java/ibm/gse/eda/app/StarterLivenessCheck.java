package ibm.gse.eda.app;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import ibm.gse.eda.app.infrastructure.kafka.OrderEventsAgent;

@Liveness
@ApplicationScoped
public class StarterLivenessCheck implements HealthCheck {

	@Inject
	OrderEventsAgent orderEventsAgent;
	
    public boolean isAlive() {
    	boolean evalIt = orderEventsAgent.isRunning();
        return evalIt;
    }
	
    @Override
    public HealthCheckResponse call() {
        boolean up = isAlive();
        return HealthCheckResponse.named(this.getClass().getSimpleName()).state(up).build();
    }
    
}
