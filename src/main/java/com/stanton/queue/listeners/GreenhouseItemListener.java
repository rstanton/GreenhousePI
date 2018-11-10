package com.stanton.queue.listeners;

import java.util.logging.Logger;

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
		logger.info("Got Notified of new Queue Event");
	
		try{
			SensorReading r = queue.take();
			logger.info("Temp: "+r.getTemp());
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void itemRemoved(ItemEvent<SensorReading> item) {
		// TODO Auto-generated method stub
		
	}

}
