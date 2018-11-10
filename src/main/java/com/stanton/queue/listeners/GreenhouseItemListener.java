package com.stanton.queue.listeners;

import java.util.logging.Logger;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.stanton.i2c.sensor.SensorReading;

public class GreenhouseItemListener implements ItemListener<SensorReading> {

	@Override
	public void itemAdded(ItemEvent<SensorReading> item) {
		Logger.getLogger("Test").info("Got Reading: "+item.getItem().getTemp());;
	}

	@Override
	public void itemRemoved(ItemEvent<SensorReading> item) {
		// TODO Auto-generated method stub
		
	}

}
