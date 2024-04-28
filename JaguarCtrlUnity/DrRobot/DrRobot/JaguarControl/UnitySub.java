package DrRobot.JaguarControl;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.IMqttDeliveryToken;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;



public class UnitySub {
	
	public String topic;
	public String broker;
	public String clientId;
	private robotSocket sock;
	private boolean firstCommand = true;
	MemoryPersistence persistence;
	MqttClient client;
	
	private String currentMessage;
	
	public void SetSocket(robotSocket s) {
		sock = s;
	}
	
	public UnitySub(String t, String b, String c, robotSocket s) {
		
		topic = t;
		broker = b;
		clientId = c;
		sock = s;
		
		persistence = new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(broker, clientId, persistence);
			MqttConnectionOptions connOpts = new MqttConnectionOptions();
			
			client.setCallback(new MqttCallback() {
		      public void connectionLost(Throwable cause) {}

		      public void messageArrived(String topic, MqttMessage message) throws Exception {
		          System.out.println("Message: " + message.toString());
		          System.out.println(sock);
		    	  currentMessage = message.toString();
		    	  
		    	  if(currentMessage.equals("RELEASE_ESTOP")) {
		    		  
		    		  System.out.println("MMW !MG");
		    		  sock.sendCommand("MMW !MG");
		    		  
		    	  }
		    	  else {
		    	  
			    	  String[] cmdArr = currentMessage.split(" ");
			    	  float forwardInput = Float.parseFloat(cmdArr[0]);
			    	  float turnInput = Float.parseFloat(cmdArr[1]);
			    	  float lightInput = Float.parseFloat(cmdArr[2]);
			    	  
			    	  int power = 20;
			    	  
			    	  int leftWheels = 0;
			    	  int rightWheels = 0;
			    	  
			    	  leftWheels = (int)(forwardInput * power);
			    	  rightWheels = (int)(forwardInput * -power);
			    	  
			    	  leftWheels += (int)(turnInput * power);
			    	  rightWheels += (int)(turnInput * power);
			    	  
			    	  System.out.println("BEFORE");
			    	  
			    	  sock.sendCommand("MMW !MG");
			    	  
			    	  System.out.println("AFTER");
			    	  
			    	  Thread.sleep(1);
			    	  
			    	  System.out.println("BEFORE2");
			    	  String strCmd = "MMW !M " + Integer.toString(leftWheels) + " " + Integer.toString(rightWheels);
			    	  System.out.println(strCmd);
			          sock.sendCommand(strCmd);
			          Thread.sleep(1);
			          sock.sendCommand("MMW !MG");
			          System.out.println("AFTER2");
			          
		    	  }
		      }

		      public void deliveryComplete(IMqttDeliveryToken token) {}

			@Override
			public void disconnected(MqttDisconnectResponse disconnectResponse) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mqttErrorOccurred(MqttException exception) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deliveryComplete(IMqttToken token) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void authPacketArrived(int reasonCode, MqttProperties properties) {
				// TODO Auto-generated method stub
				
			}
		    });
			
			connOpts.setCleanStart(true);
			client.connect(connOpts);
			client.subscribe(topic, 0);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
