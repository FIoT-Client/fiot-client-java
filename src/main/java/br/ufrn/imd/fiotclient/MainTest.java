package br.ufrn.imd.fiotclient;

import java.io.IOException;
import java.util.HashMap;

import org.ini4j.InvalidFileFormatException;

import br.ufrn.imd.fiotclient.context.FiwareContextClient;
import br.ufrn.imd.fiotclient.iot.FiwareIotClient;

public class MainTest {
	
	public static void main(String[] args) throws InvalidFileFormatException, IOException {
		FiwareContextClient fiwareContextClient = new FiwareContextClient("config.ini");
		System.out.println(fiwareContextClient.getEntityById("RFID_READER_001"));
		
		FiwareIotClient fiwareIotClient = new FiwareIotClient("config.ini");
		HashMap<String, String> measurementGroup = new HashMap<>();
		measurementGroup.put("r", "1234ABCD5678EFGH");
		fiwareIotClient.sendObservation("RFID_READER_001", measurementGroup, "MQTT");
		System.out.println("FInished");
		
		System.out.println(fiwareContextClient.getEntityById("RFID_READER_001"));
	}

}
