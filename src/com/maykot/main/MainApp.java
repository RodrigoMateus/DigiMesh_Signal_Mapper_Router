package com.maykot.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.utils.DeviceConfig;
import com.digi.xbee.api.utils.LogRecord;
import com.digi.xbee.api.utils.SerialPorts;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
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
		testSelect();
	}

	public static void openMyDevice() {
		try {
			XTEND_PORT = deviceConfig.getXTendPort();
			myDevice = openDevice(XTEND_PORT, XTEND_BAUD_RATE);
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

	public static void sendTestMessage() {
		byte[] dataToSend = new String(
				"As diversas finalidades do trabalho acadêmico podem se resumir em apresentar, demonstrar, difundir, recuperar ou contestar o conhecimento produzido, acumulado ou transmitido. Ao apresentar resultados, o texto acadêmico atende à necessidade de publicidade relativa ao processo de conhecimento. A pesquisa realizada, a ideia concebida ou a dedução feita perecem se não vierem a público; por esse motivo existem diversos canais de publicidade adequados aos diferentes trabalhos: as defesas públicas, os periódicos, as comunicações e a multimídia virtual são alguns desses. A demonstração do conhecimento é necessidade na comunidade acadêmica, onde esse conhecimento é o critério de mérito e acesso. Assim, existem as provas, concursos e diversos outros processos de avaliação pelos quais se constata a construção ou transmissão do saber. Difundir o conhecimento às esferas externas à comunidade acadêmica é atividade cada vez mais presente nas instituições de ensino, pesquisa e extensão, e o texto correspondente a essa prática tem característica própria sem abandonar a maior parte dos critérios de cientificidade. A recuperação do conhecimento é outra finalidade do texto acadêmico. Com bastante freqüência, parcelas significativas do conhecimento caem no esquecimento das comunidades e das pessoas; a recuperação e manutenção ativa da maior diversidade de saberes é finalidade importante de atividades científicas objeto da produção de texto. Quase todo conhecimento produzido é contestado. Essa contestação, em que não constitua conhecimento diferenciado, certamente é etapa contribuinte no processo da construção do saber que contesta, quer por validá-lo, quer por refutá-lo. As finalidades do texto acadêmico certamente não se esgotam nessas, mas ficam aqui exemplificadas. Para atender à diversidade dessas finalidades, existe a multiplicidade de formas, entre as quais se encontram alguns conhecidos tipos, sobre os quais se estabelece conceito difuso.")
						.getBytes();
		try {
			Router.getInstance().sendMessage(myDevice, remoteDevice, MessageParameter.SEND_TXT_FILE, dataToSend);
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

	public static void testSelect() {

		int option;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite uma opção:");
		System.out.println("1: Envia texto Curto.");
		System.out.println("2: Envia texto Longo.");
		System.out.println("3: Envia HELLO.");
		option = scanner.nextInt();
		scanner.close();

		switch (option) {
		case 1:
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

		case 2:
			try {
				byte[] dataToSend = Files.readAllBytes(Paths.get("Texto_Longo.txt"));
				System.out.println(new String(dataToSend));
				for (int i = 0; i < 3; i++) {
					try {
						System.out.println("Enviando Texto Longo " + (i + 1));
						Router.getInstance().sendMessage(myDevice, remoteDevice, MessageParameter.SEND_TXT_FILE,
								dataToSend);
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				e.printStackTrace();
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				e.printStackTrace();
			}
			break;

		case 3:
			byte[] dataToSend = new String("Hello!!!").getBytes();
			try {
				Router.getInstance().sendMessage(myDevice, remoteDevice, MessageParameter.SEND_TXT_FILE, dataToSend);
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				e.printStackTrace();
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}