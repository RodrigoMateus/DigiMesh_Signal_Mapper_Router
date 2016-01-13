package com.maykot.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.SerializationUtils;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.IProcessMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyResponse;
import com.maykot.radiolibrary.RadioRouter;

public class ProcessMessage implements IProcessMessage {

	private RadioRouter routerRadio;

	public ProcessMessage() {
		this(RadioRouter.getInstance());
	}

	public ProcessMessage(RadioRouter routerRadio) {
		this.routerRadio = routerRadio;
	}

	@Override
	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		String fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + ".txt";
		try {
			FileOutputStream fileChannel = new FileOutputStream(fileName);
			fileChannel.write(message);
			fileChannel.close();
			routerRadio.sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_TXT_FILE,
					new String("Texto enviado com SUCESSO!").getBytes());
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: ERRO ao criar arquivo texto");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void textFileConfirm(byte[] message) {
		System.out.println(new String(message));
	}

	@Override
	public void localPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void localPostConfirm(byte[] message) {
		ProxyResponse proxyResponse = (ProxyResponse) SerializationUtils.deserialize(message);
		System.out.println("Local POST Response: " + proxyResponse.getStatusCode() + " - "
				+ ErrorMessage.get(proxyResponse.getStatusCode()).description());
	}

	@Override
	public void mobilePostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		ProxyResponse proxyResponse = (ProxyResponse) SerializationUtils.deserialize(message);
		System.out.println("Mobile POST Response: " + proxyResponse.getStatusCode() + " - "
				+ ErrorMessage.get(proxyResponse.getStatusCode()).description());
	}

	@Override
	public void mobilePostConfirm(byte[] message) {
		ProxyResponse proxyResponse = (ProxyResponse) SerializationUtils.deserialize(message);
		System.out.println("Mobile POST Response: " + proxyResponse.getStatusCode() + " - "
				+ ErrorMessage.get(proxyResponse.getStatusCode()).description());
	}

}
