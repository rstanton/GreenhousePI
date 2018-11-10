package com.stanton.queue.listeners;

import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.stanton.i2c.sensor.SensorReading;

public class GreenhouseItemListener implements ItemListener<SensorReading> {
	IQueue<SensorReading> queue;
	Logger logger;
	
	public GreenhouseItemListener() {
		HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        queue = hz.getQueue( "greenhouse" );
        
        logger = Logger.getLogger(this.getClass().getName());
	}
	
	@Override
	public void itemAdded(ItemEvent<SensorReading> item) {
		try{
			SensorReading r = queue.take();
			sendtoCloud(r);
			logger.info("Temperature: "+r.getTemp());
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Use Jersey to send the data to thingspeak.
	 * @param r
	 * @throws Exception
	 */
	private void sendtoCloud(SensorReading r) throws Exception{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("https://api.thingspeak.com").
				path("update.json").
				queryParam("api_key","OMFTOKIBILJ17SRF").
				queryParam("field1", r.getTemp());
		
		target.request().get();
	}

	@Override
	public void itemRemoved(ItemEvent<SensorReading> item) {
		
	}

}
