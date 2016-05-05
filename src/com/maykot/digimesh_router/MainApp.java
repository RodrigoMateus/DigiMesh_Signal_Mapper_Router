package com.maykot.digimesh_router;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.RadioTransmiter;
import com.maykot.radiolibrary.interfaces.MyRadio;
import com.maykot.radiolibrary.model.MessageParameter;
import com.maykot.radiolibrary.mqtt.MqttRouter;
import com.maykot.radiolibrary.utils.DeviceConfig;
import com.maykot.radiolibrary.utils.DiscoverRemoteDevice;
import com.maykot.radiolibrary.utils.LogRecord;
import com.maykot.radiolibrary.utils.OpenMyDevice;

public class MainApp {

	/* Arquivo de configurações do sistema */
	static DeviceConfig deviceConfig;

	/* Rádios */
	public static MyRadio receiverDevice;
	public static MyRadio senderDevice;
	public static RemoteXBeeDevice remoteDevice;

	/* MQTT */
	static String BROKER_URL = null;
	static String CLIENT_ID = null;
	static String SUBSCRIBED_TOPIC = null;
	static int QoS = -1;
	static MqttClient mqttClient;

	public static void main(String[] args) {
		System.out.println(" +---------------------------------+");
		System.out.println(" |  DigiMesh Signal Mapper Router  |");
		System.out.println(" +---------------------------------+\n");

		new LogRecord();

		deviceConfig = DeviceConfig.getInstance();
		receiverDevice = OpenMyDevice.open(deviceConfig, new ProcessMessage());
		/* Sender = Receiver */
		senderDevice = receiverDevice;

		try {
			remoteDevice = DiscoverRemoteDevice.discover(deviceConfig, receiverDevice);
		} catch (XBeeException e2) {
			e2.printStackTrace();
		}

		try {
			mqttClient = MqttRouter.getInstance().setMqttRouter(deviceConfig, senderDevice, remoteDevice);
		} catch (MqttException e) {
			e.printStackTrace();
		}

		// Envia uma mensagem para testar a conexão.
		try {
			new RadioTransmiter().sendMessage(senderDevice, remoteDevice, MessageParameter.SEND_CLIENT_CONNECTION,
					new String(senderDevice.getNodeID() + " client is connected!").getBytes());
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (XBeeException e) {
			e.printStackTrace();
		}

		// Menu para seleção de Testes
		TestRouter.showMenu();
	}
}