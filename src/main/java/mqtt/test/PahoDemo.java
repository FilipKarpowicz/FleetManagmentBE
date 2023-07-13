package mqtt.test;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class PahoDemo implements MqttCallback {

        MqttClient client;

        public PahoDemo() {
        }

        public static void main(String[] args) {
                new PahoDemo().doDemo();
        }

        public void doDemo() {
                try {
                        String username   = "fleet-management@ttn";
                        String password   = "NNSXS.PFPK54OAZBNI3NC7QVP6SCZ6RPKBH3ERMXKCZ3Q.QEV55526VGK4JRBOE2MXJ7ACHG3BK4CVIPCTUX6VP3Q5WO6KBXOQ";
                        String serverurl  = "tcp://eu1.cloud.thethings.network:1883";
                        String clientId   = MqttClient.generateClientId();

                        MqttConnectOptions options = new MqttConnectOptions();
                        options.setCleanSession(true);
                        options.setUserName(username);
                        options.setPassword(password.toCharArray());

                        client = new MqttClient(serverurl, clientId, null);
                        client.connect(options);

                        client.setCallback(this);
                        client.subscribe("#");
                } catch (MqttException e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void connectionLost(Throwable cause) {
                // TODO Auto-generated method stub

        }

        @Override
        public void messageArrived(String topic, MqttMessage message)
                throws Exception {
                System.out.println(message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
                // TODO Auto-generated method stub

        }

}