package com.maykot.main;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.DeviceConfig;
import com.digi.xbee.api.utils.LogRecord;
import com.maykot.mqtt.RouterMqtt;
import com.maykot.radiolibrary.DiscoverRemoteDevice;
import com.maykot.radiolibrary.OpenMyDevice;
import com.maykot.radiolibrary.RouterRadio;

public class MainApp {

	/* Arquivo de configurações do sistema */
	static DeviceConfig deviceConfig;

	/* XTends */
	public static DigiMeshDevice myDevice;
	public static RemoteXBeeDevice remoteDevice;

	/* MQTT */
	static String BROKER_URL = null;
	static String CLIENT_ID = null;
	static String SUBSCRIBED_TOPIC = null;
	static int QoS = -1;
	static MqttClient mqttClient;

	public static void main(String[] args) {
		System.out.println(" +-------------------+");
		System.out.println(" |  DigiMesh Router  |");
		System.out.println(" +-------------------+\n");

		try {
			deviceConfig = new DeviceConfig();

			BROKER_URL = deviceConfig.getBrokerURL();
			CLIENT_ID = deviceConfig.getClientId();
			SUBSCRIBED_TOPIC = deviceConfig.getSubscribedTopic();
			QoS = deviceConfig.getQoS();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		new LogRecord();

		myDevice = OpenMyDevice.open(deviceConfig);

		// Registra listener para processar mensagens recebidas
		RouterRadio.getInstance().processMyMessage(new ProcessMessage());

		try {
			remoteDevice = DiscoverRemoteDevice.discover(deviceConfig, myDevice);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (XBeeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, null);
			mqttClient.setCallback(new RouterMqtt());
			mqttClient.connect();
			mqttClient.subscribe(SUBSCRIBED_TOPIC, QoS);
		} catch (MqttException e) {
			e.printStackTrace();
		}

		// Menu para seleção de Testes
		TestRouter.showMenu();
	}

}