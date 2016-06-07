package com.maykot.digimesh_router;

import com.maykot.radiolibrary.model.ProxyResponse;

public class RadioCkeck {

	/**
	 * Retorna o nivel de potencia do proxy para o cliente
	 * @param proxyResponse
	 * @param rssi
	 * @return
	 */
	public static ProxyResponse radioCheck(ProxyResponse proxyResponse, int rssi){
		proxyResponse.setBody(new String("{rssi:"+rssi+"}").getBytes());
		return proxyResponse;
		
	}
	
}
