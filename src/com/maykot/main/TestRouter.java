package com.maykot.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.lang3.SerializationUtils;

import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyRequest;
import com.maykot.radiolibrary.RadioRouter;

public class TestRouter {

	public static void showMenu() {

		int option;
		Scanner scanner = new Scanner(System.in);

		do {
			System.out.println("Digite uma opção:");
			System.out.println("1: Envia HELLO.");
			System.out.println("2: Envia texto Curto.");
			System.out.println("3: Envia texto Longo.");
			System.out.println("4: Envia HTTP Post.");

			option = scanner.nextInt();
			switch (option) {
			case 1:
				try {
					byte[] dataToSend = new String("Hello!!!").getBytes();
					RadioRouter.getInstance().sendMessage(MainApp.myDevice, MainApp.remoteDevice,
							MessageParameter.SEND_TXT_FILE, dataToSend);
				} catch (TimeoutException e) {
					System.out.println("Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": "
							+ ErrorMessage.TIMEOUT_ERROR.description());
					e.printStackTrace();
				} catch (XBeeException e) {
					System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
							+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
					e.printStackTrace();
				}
				break;

			case 2:
				for (int i = 0; i < 3; i++) {
					try {
						System.out.println("Enviando Texto Curto " + (i + 1));
						sendTestMessage();
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			case 3:
				for (int i = 0; i < 3; i++) {
					try {
						byte[] dataToSend = Files.readAllBytes(Paths.get("Texto_Longo.txt"));
						System.out.println("Enviando Texto Longo " + (i + 1));
						RadioRouter.getInstance().sendMessage(MainApp.myDevice, MainApp.remoteDevice,
								MessageParameter.SEND_TXT_FILE, dataToSend);
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						System.out.println("Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": "
								+ ErrorMessage.TIMEOUT_ERROR.description());
						e.printStackTrace();
					} catch (XBeeException e) {
						System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
								+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
						e.printStackTrace();
					}
				}
				break;

			case 4:
				sendLocalPost();
				break;

			default:
				break;
			}
		} while (option != 0);
		scanner.close();
	}

	public static void sendTestMessage() {
		byte[] dataToSend = new String(
				"As diversas finalidades do trabalho acadêmico podem se resumir em apresentar, demonstrar, difundir, recuperar ou contestar o conhecimento produzido, acumulado ou transmitido. Ao apresentar resultados, o texto acadêmico atende à necessidade de publicidade relativa ao processo de conhecimento. A pesquisa realizada, a ideia concebida ou a dedução feita perecem se não vierem a público; por esse motivo existem diversos canais de publicidade adequados aos diferentes trabalhos: as defesas públicas, os periódicos, as comunicações e a multimídia virtual são alguns desses. A demonstração do conhecimento é necessidade na comunidade acadêmica, onde esse conhecimento é o critério de mérito e acesso. Assim, existem as provas, concursos e diversos outros processos de avaliação pelos quais se constata a construção ou transmissão do saber. Difundir o conhecimento às esferas externas à comunidade acadêmica é atividade cada vez mais presente nas instituições de ensino, pesquisa e extensão, e o texto correspondente a essa prática tem característica própria sem abandonar a maior parte dos critérios de cientificidade. A recuperação do conhecimento é outra finalidade do texto acadêmico. Com bastante freqüência, parcelas significativas do conhecimento caem no esquecimento das comunidades e das pessoas; a recuperação e manutenção ativa da maior diversidade de saberes é finalidade importante de atividades científicas objeto da produção de texto. Quase todo conhecimento produzido é contestado. Essa contestação, em que não constitua conhecimento diferenciado, certamente é etapa contribuinte no processo da construção do saber que contesta, quer por validá-lo, quer por refutá-lo. As finalidades do texto acadêmico certamente não se esgotam nessas, mas ficam aqui exemplificadas. Para atender à diversidade dessas finalidades, existe a multiplicidade de formas, entre as quais se encontram alguns conhecidos tipos, sobre os quais se estabelece conceito difuso.")
						.getBytes();
		try {
			RadioRouter.getInstance().sendMessage(MainApp.myDevice, MainApp.remoteDevice,
					MessageParameter.SEND_TXT_FILE, dataToSend);
		} catch (TimeoutException e) {
			System.out.println(
					"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
			e.printStackTrace();
		} catch (XBeeException e) {
			System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
					+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			e.printStackTrace();
		}
	}

	public static void sendLocalPost() {
		ProxyRequest proxyRequest = new ProxyRequest();
		proxyRequest.setVerb("POST");
		proxyRequest.setUrl("http://localhost:8000");

		HashMap<String, String> header = new HashMap<String, String>();
		header.put("content-type", "image/jpg");
		header.put("proxy-response", "0");
		proxyRequest.setHeader(header);
		proxyRequest
				.setIdMessage("IdMessage_" + new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date())));

		byte[] proxyRequestBody = null;
		try {
			proxyRequestBody = Files.readAllBytes(new File("image50KB.jpg").toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxyRequest.setBody(proxyRequestBody);

		byte[] dataToSend = SerializationUtils.serialize(proxyRequest);

		try {
			RadioRouter.getInstance().sendMessage(MainApp.myDevice, MainApp.remoteDevice,
					MessageParameter.SEND_LOCAL_POST, dataToSend);
		} catch (TimeoutException e) {
			System.out.println(
					"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
			e.printStackTrace();
		} catch (XBeeException e) {
			System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
					+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			e.printStackTrace();
		}
	}

}
