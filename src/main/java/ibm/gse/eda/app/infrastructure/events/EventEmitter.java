package ibm.gse.eda.app.infrastructure.events;

public interface EventEmitter {

    public void emit(OrderEvent event) throws Exception;
    public void safeClose();

}
