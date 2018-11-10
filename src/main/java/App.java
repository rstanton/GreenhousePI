import com.hazelcast.config.Config;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.QueueConfig;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.stanton.i2c.sensor.BMP180;
import com.stanton.queue.listeners.GreenhouseItemListener;

/*
 * Flow will be
 * - Start a thread
 * -   Thread will read from sensor
 * -   Temp will be put to an in memory queue (Hazelcast)
 * -   API client will read from queue
 * -   API client will put to API
 * -   API client will remove from queue when succesful
 */
public class App extends Thread{

    public static void main(String[] args) {
    	Config config = new Config();
    	QueueConfig qc = config.getQueueConfig("default");
    	
    	ItemListenerConfig ic = new ItemListenerConfig(new GreenhouseItemListener(), true);
    	
    	qc.setName("greenhouse").addItemListenerConfig(ic);
    	
        new App().start();
    }
    
    public void run() {
    	while(true) {
    		try {
    			new BMP180().read();
    			Thread.sleep(5000);;
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
}
