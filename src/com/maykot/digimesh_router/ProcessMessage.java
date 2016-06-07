package com.maykot.digimesh_router;

import org.apache.commons.lang3.SerializationUtils;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.ByteUtils;
import com.maykot.radiolibrary.interfaces.IProcessMessage;
import com.maykot.radiolibrary.model.ErrorMessage;
import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.mqtt.MqttMessageSender;

public class ProcessMessage implements IProcessMessage {

	public ProcessMessage() {}

	@Override
	public void clientConnectionReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {}

	@Override
	public void clientConnectionConfirm(byte[] message) {
		System.out.println(new String(message));
	}

	@Override
	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {}

	@Override
	public void textFileConfirm(byte[] message) {
		byte[] rssi;
		try {
			rssi = MainApp.receiverDevice.getParameter("DB");
			System.out.println(new String("RSSI Value: " + ByteUtils.byteArrayToInt(rssi)));
		} catch (XBeeException e) {}
	}

	@Override
	public void localPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {}

	@Override
	public void localPostConfirm(byte[] message) {
		ProxyResponse proxyResponse = (ProxyResponse) SerializationUtils.deserialize(message);
		System.out.println("Local POST Response: " + proxyResponse.getStatusCode() + " - "
				+ ErrorMessage.get(proxyResponse.getStatusCode()).description());
	}

	@Override
	public void proxyMessageReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {}

	@Override
	public void proxyMessageConfirm(byte[] message, int rssi) {
		ProxyResponse proxyResponse = (ProxyResponse) SerializationUtils.deserialize(message);

		String clientId = proxyResponse.getMqttClientId();
		String messageId = proxyResponse.getIdMessage();

		//if(proxyResponse.getVerb().contains("check")){
		//	RadioCkeck.radioCheck(proxyResponse, rssi);
		//}
		
		new MqttMessageSender().sendResponseMessage(MainApp.mqttClient, clientId, messageId, message);
		System.out.println("Mobile POST Response: " + proxyResponse.getStatusCode() + " - "
				+ ErrorMessage.get(proxyResponse.getStatusCode()).description());
	}

	@Override
	public void packetTransferReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message, int rssi) {}

	@Override
	public void packetTransferConfirm(byte[] message) {}

	public void telemetryTransferReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message, int rssi) {}

	@Override
	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message) {}

}
