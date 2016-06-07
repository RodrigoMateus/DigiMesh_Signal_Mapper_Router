package com.maykot.digimesh_router;

import com.digi.xbee.api.exceptions.TimeoutException;
import com.maykot.radiolibrary.interfaces.MyRadio;
import com.maykot.radiolibrary.utils.OpenMyDevice;

public class RadioMonitor implements Runnable {

	public MyRadio myRadio;

	public RadioMonitor(MyRadio myRadio) {
		this.myRadio = myRadio;
	}

	@Override
	public void run() {
		while (true) {
			checkProxyRadio();
			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
			}
		}
	}

	public void checkProxyRadio() {
		try {
			myRadio.getPowerLevel().toString();
		} catch (TimeoutException e) {
			MainApp.receiverDevice = OpenMyDevice.open(MainApp.deviceConfig, new ProcessMessage());
			MainApp.senderDevice = MainApp.receiverDevice;

		} catch (Exception e) {
			MainApp.receiverDevice = OpenMyDevice.open(MainApp.deviceConfig, new ProcessMessage());
			MainApp.senderDevice = MainApp.receiverDevice;
		}

	}
}
