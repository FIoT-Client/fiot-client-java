package br.ufrn.imd.fiotclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import jdk.nashorn.internal.runtime.JSONListAdapter;
import org.ini4j.InvalidFileFormatException;

import br.ufrn.imd.fiotclient.context.FiwareContextClient;
import br.ufrn.imd.fiotclient.iot.FiwareIotClient;

public class MainTest {
	
	public static void main(String[] args) throws InvalidFileFormatException, IOException {
		FiwareContextClient fiwareContextClient = new FiwareContextClient("config.ini");
		FiwareIotClient fiwareIotClient = new FiwareIotClient("config.ini");

        fiwareIotClient.listDevices();
        System.out.println();

        fiwareIotClient.registerDeviceFromFile("DISTANCE_SENSOR.json", "DISTANCE_SENSOR_001", "DISTANCE_SENSOR_001", "", "IoTA-UL");
        System.out.println();

        fiwareIotClient.listDevices();
        System.out.println();

        fiwareIotClient.removeDevice("DISTANCE_SENSOR_001");
        System.out.println();

        fiwareIotClient.listDevices();
        System.out.println();

		fiwareContextClient.getEntityById("RFID_READER_001");
        System.out.println();

		HashMap<String, String> measurementGroup = new HashMap<>();
		measurementGroup.put("r", "1234ABCD5678EFGH");
		fiwareIotClient.sendObservation("RFID_READER_001", measurementGroup, "MQTT");
        System.out.println();

		fiwareIotClient.sendCommand("GATE_001", "GATE_001", "change_state", Arrays.asList("OPEN"));
        System.out.println();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fiwareIotClient.sendCommand("GATE_001", "GATE_001", "change_state", Arrays.asList("CLOSED"));
        System.out.println();

		fiwareContextClient.getEntityById("RFID_READER_001");
        System.out.println();

		fiwareContextClient.listSubscriptions();
        System.out.println();
	}

}
