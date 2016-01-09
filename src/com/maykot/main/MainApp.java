package com.maykot.main;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.mqtt.RouterMqtt;
import com.maykot.radiolibrary.DiscoverRemoteDevice;
import com.maykot.radiolibrary.OpenMyDevice;
import com.maykot.radiolibrary.RouterRadio;
import com.maykot.utils.DeviceConfig;
import com.maykot.utils.LogRecord;

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
			deviceConfig = DeviceConfig.getInstance();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			mqttClient = new RouterMqtt(myDevice, remoteDevice).connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Menu para seleção de Testes
		TestRouter.showMenu();
	}

}