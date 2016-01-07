package com.maykot.mqtt;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.main.MainApp;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyResponse;
import com.maykot.radiolibrary.RouterRadio;

public class RouterMqtt implements MqttCallback {
	private RouterRadio routerRadio;

	public RouterMqtt() {
		this(RouterRadio.getInstance());
	}

	public RouterMqtt(RouterRadio routerRadio) {
		this.routerRadio = routerRadio;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// Se a mensagem contém um HTTP POST
		if (topic.toLowerCase().contains("http_post")) {

			String[] topicWords = topic.split("/");
			String clientId = topicWords[2];
			byte[] mqttClientId = clientId.getBytes();
			String messageId = topicWords[3];
			String body = null;

			byte[] dataToSend = message.getPayload();

			try {
				routerRadio.sendMessage(MainApp.myDevice, MainApp.remoteDevice, MessageParameter.SEND_HTTP_POST,
						dataToSend);
			} catch (TransmitException e) {
				System.out.println(
						ErrorMessage.TRANSMIT_EXCEPTION.value() + ": " + ErrorMessage.TRANSMIT_EXCEPTION.description());
				sendErrorMessage(ErrorMessage.TRANSMIT_EXCEPTION.value(), clientId, messageId,
						ErrorMessage.TRANSMIT_EXCEPTION.description());
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				sendErrorMessage(ErrorMessage.TIMEOUT_ERROR.value(), clientId, messageId,
						ErrorMessage.TIMEOUT_ERROR.description());
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.XBEE_EXCEPTION_ERROR.value(), clientId, messageId,
						ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			} catch (Exception e) {
				System.out.println("Erro " + ErrorMessage.EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.EXCEPTION_ERROR.value(), clientId, messageId,
						ErrorMessage.EXCEPTION_ERROR.description());
			}
		}
	}

	public void sendErrorMessage(int statusCode, String clientId, String messageId, String errorCode) {
		ProxyResponse errorResponse = new ProxyResponse(statusCode, "application/json", errorCode.getBytes());
		errorResponse.setMqttClientId(clientId);
		errorResponse.setIdMessage(messageId);

		byte[] payload = SerializationUtils.serialize(errorResponse);

		new MQTTMonitor().sendMQTT(errorResponse, payload);
	}
}
