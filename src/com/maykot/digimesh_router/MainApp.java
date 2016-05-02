package com.maykot.digimesh_router;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.interfaces.MyRadio;
import com.maykot.radiolibrary.model.MessageParameter;
import com.maykot.radiolibrary.mqtt.MqttRouter;
import com.maykot.radiolibrary.utils.DeviceConfig;
import com.maykot.radiolibrary.utils.DiscoverRemoteDevice;
import com.maykot.radiolibrary.utils.LogRecord;
import com.maykot.radiolibrary.utils.OpenMyDevice;

@SuppressWarnings("unused")
public class MainApp {

	/* Arquivo de configurações do sistema */
	static DeviceConfig deviceConfig;

	public static MyRadio myDevice = null;

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
		myDevice = OpenMyDevice.open(deviceConfig);

		// Registra listener para processar mensagens recebidas
		RadioRouter.getInstance().addProcessMessageListener(new ProcessMessage());

		try {
			remoteDevice = DiscoverRemoteDevice.discover(deviceConfig, myDevice);
		} catch (XBeeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			mqttClient = MqttRouter.getInstance().setMqttRouter(deviceConfig, myDevice, remoteDevice);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Envia uma mensagem para testar a conexão.
		try {
			RadioRouter.getInstance().sendMessage(myDevice, remoteDevice, MessageParameter.SEND_CLIENT_CONNECTION,
					new String(myDevice.getNodeID() + " client is connected!").getBytes());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Menu para seleção de Testes
		TestRouter.showMenu();
	}
}