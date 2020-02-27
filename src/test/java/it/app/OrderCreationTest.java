package it.app;

import static org.junit.Assert.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ibm.gse.eda.app.dto.OrderParameters;
import ibm.gse.eda.app.infrastructure.events.Address;

public class OrderCreationTest {

	private static String baseUrl;
   	private static final String ORDERS_ENDPOINT = "/orders";
   	private Client client;
   	private Response response;
   	private Jsonb jsonb = JsonbBuilder.create();
   	
   	@BeforeClass
    public static void oneTimeSetup() {
        String port = System.getProperty("liberty.test.port","9080");
        baseUrl = "http://localhost:" + port;
    }
	    
    @Before
    public void setup() {
        response = null;
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }
   
    @After
    public void teardown() {
    	if (response != null) response.close();
        client.close();
    }

    @Test
    public void shouldCreateOrder() {
    	Address destinationAddress = new Address("Street", "City", "DestinationCountry", "State", "Zipcode");
    	OrderParameters inOrder = new OrderParameters("C11", "P02",10, destinationAddress);
    	
    	String inOrderJson = jsonb.toJson(inOrder);
        response =  client.target(baseUrl + ORDERS_ENDPOINT).request().post(Entity.entity(inOrderJson, MediaType.APPLICATION_JSON_TYPE));
        assertEquals("Incorrect response code", 200, response.getStatus());
    }
}
