package com.maykot.main;

import java.io.IOException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.utils.DeviceConfig;
import com.digi.xbee.api.utils.LogRecord;
import com.digi.xbee.api.utils.SerialPorts;
import com.maykot.radiolibrary.Router;

public class MainApp {

	/* XTends */
	public static DigiMeshDevice myDevice;
	public static RemoteXBeeDevice remoteDevice;
	public static DeviceConfig deviceConfig;
	static String XTEND_PORT = null;
	static int XTEND_BAUD_RATE;
	static int TIMEOUT_FOR_SYNC_OPERATIONS = 10000; // 10 seconds
	static String REMOTE_NODE_IDENTIFIER = null;

	public static void main(String[] args) {
		System.out.println(" +-------------------+");
		System.out.println(" |  DigiMesh Router  |");
		System.out.println(" +-------------------+\n");

		try {
			deviceConfig = new DeviceConfig();
			XTEND_PORT = deviceConfig.getXTendPort();
			XTEND_BAUD_RATE = deviceConfig.getXTendBaudRate();
			REMOTE_NODE_IDENTIFIER = deviceConfig.getRemoteNodeID();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		new LogRecord();

		openMyDevice();

		// Registra listener para processar mensagens recebidas
		Router.getInstance().processMyMessage(new ProcessMessage());

		try {
			discoverDevice();
		} catch (XBeeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Menu para seleção de Testes
		TestRouter.showMenu();
	}

	public static void openMyDevice() {
		try {
			XTEND_PORT = deviceConfig.getXTendPort();
			myDevice = openDevice(XTEND_PORT, XTEND_BAUD_RATE);
			myDevice.setReceiveTimeout(TIMEOUT_FOR_SYNC_OPERATIONS);
			myDevice.addExplicitDataListener(Router.getInstance());
			System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
					+ myDevice.getPowerLevel() + ").");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String port : SerialPorts.getSerialPortList()) {
			try {
				System.out.println("Try " + port);
				myDevice = openDevice(port, XTEND_BAUD_RATE);
				myDevice.addExplicitDataListener(Router.getInstance());
				System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel: "
						+ myDevice.getPowerLevel() + ").");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("openDevice() ERROR");
			}
		}
		System.out.println("LOCAL Radio not found! Try openDevice() again.");
		openMyDevice();
	}

	public static DigiMeshDevice openDevice(String port, int bd) throws Exception {
		DigiMeshDevice device = new DigiMeshDevice(port, bd);
		device.open();
		device.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
		return device;
	}

	public static void discoverDevice() throws XBeeException {
		// Obtain the remote XBee device from the XBee network.
		XBeeNetwork xbeeNetwork = myDevice.getNetwork();

		do {
			remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);
			if (remoteDevice == null) {
				System.out.println("Couldn't find the Radio " + REMOTE_NODE_IDENTIFIER + ".");
			}
		} while (remoteDevice == null);

		System.out.println("Was found REMOTE radio " + REMOTE_NODE_IDENTIFIER + " (PowerLevel "
				+ remoteDevice.getPowerLevel() + ").");
	}

}