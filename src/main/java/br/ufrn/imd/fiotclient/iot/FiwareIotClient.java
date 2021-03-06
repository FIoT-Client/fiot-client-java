package br.ufrn.imd.fiotclient.iot;

import br.ufrn.imd.fiotclient.SimpleClient;
import br.ufrn.imd.fiotclient.utils.ConfigParser;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.ini4j.InvalidFileFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//import logging

//__copyright__ = "Copyright 2017, Lucas Cristiano Calixto Dantas"
//__credits__ = ["Lucas Cristiano Calixto Dantas"]
//__license__ = "MIT"
//__version__ = "0.1.0"
//__maintainer__ = "Lucas Cristiano Calixto Dantas"
//__email__ = "lucascristiano27@gmail.com"
//__status__ = "Development"

/*
 * Client for doing IoT management operations on FIWARE platform
 * 
 * @author Lucas Cristiano Calixto Dantas
 */
public class FiwareIotClient extends SimpleClient {

    private String idasHost;
    private String idasAdminPort;
    private String idasUL20Port;
    private String apiKey;
    private String mosquittoHost;
    private String mosquittoPort;

    /*
     * @param configFile  The file in which load the default configuration
     */
    public FiwareIotClient(String configFile) throws InvalidFileFormatException, IOException {
        super(configFile);

        Map<String, String> configMap = ConfigParser.readConfigFile(configFile);

        this.idasHost = configMap.get("idas_host");
        this.idasAdminPort = configMap.get("idas_admin_port");
        this.idasUL20Port = configMap.get("idas_ul20_port");
        this.apiKey = configMap.get("api_key");

        this.mosquittoHost = configMap.get("mosquitto_host");
        this.mosquittoPort = configMap.get("mosquitto_port");
    }

    /*
     * Generate a random api key to be used on service creation
     *
     * @return  The generated api key string
     */
    public static String generateApiKey() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /*
     * Creates a new service with the given information
     *
     * @param service      The name of the service to be created
     * @param servicePath  The service path of the service to be created
     * @param apiKey       A specific api key to use to create the service. If no api key is provided, a random one will be generated.
     * @return             The information of the created service
     */
    public String createService(String service, String servicePath, String apiKey) {
        System.out.println("Creating service");

        if (apiKey.equals("")) {
            apiKey = generateApiKey();
        }

        String responseStr = "";
        JSONObject response = new JSONObject();

        boolean created = false;
        while (!created) {
            responseStr = this.createServiceAux(service, servicePath, apiKey);
            response = new JSONObject(responseStr);

            if (response.getInt("status_code") == 201) {
                created = true;
            } else {
                apiKey = generateApiKey();
            }
        }
        response.put("api_key", "api_key");

        return response.toString();
    }

    /*
     * Auxiliary method to try to create a service with the given information
     *
     * @param service      The name of the service to be created
     * @param servicePath  The service path of the service to be created
     * @param apiKey       The api key to use to create the service
     * @return             The response of the creation request
     */
    private String createServiceAux(String service, String servicePath, String apiKey) {
        String url = String.format("http://%s:%s/iot/services", this.idasHost, this.idasAdminPort);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");
        additionalHeaders.put("Fiware-Service", service);
        additionalHeaders.put("Fiware-ServicePath", servicePath);

        String payload = String.format(
                "{" +
                    "\"services\":" +
                    "[" +
                        "{" +
                            "\"protocol\": [\"IoTA-UL\"]," + //TODO Remove hardcoded protocol
                            "\"apikey\": \"%s\"," +
                            "\"token\": \"token2\"," +
                            "\"cbroker\": \"http://%s:%s\"," +
                            "\"entity_type\": \"thing\"," +
                            "\"resource\": \"/iot/d\"" +
                        "}" +
                    "]" +
                "}", apiKey, this.getCbHost(), this.getCbPort());

        return this.sendRequest(url, payload, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Remove a subservice into a service. If Fiware-ServicePath is '/\*' or '/#' remove service and all its sub-services.
     *
     * @param service        The name of the service to be removed
     * @param servicePath    The service path of the service to be removed
     * @param apiKey         The api key of the service. If no value is provided, default value "" will be used
     * @param removeDevices  If either its to remove devices in service/subservice or not.
     *                       If no value is provided, the default value (False) will be used.
     *                       This parameter is not valid when Fiware-ServicePath is '/\*' or '/#'.
     * @return               The response of the removal request
     */
    public String removeService(String service, String servicePath, String apiKey, boolean removeDevices) {
        // logging.info("Removing service")

        String url = String.format("http://%s:%s/iot/services?resource=%s&apikey=%s", idasHost, idasAdminPort, "/iot/d", apiKey);

        if (!servicePath.equals("/*") && !servicePath.equals("/#")) {
            String removeDevicesStr = removeDevices? "true" : "false";
            url = url + String.format("&device=%s", removeDevicesStr);
        }

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Fiware-Service", service);
        additionalHeaders.put("Fiware-ServicePath", servicePath);

        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.DELETE, additionalHeaders);
    }

    /*
     * Get all registered services
     *
     * @return  A list with the registered services
     */
    public void listServices() {
        // TODO Implement
    }

    /*
     * Register a new device with the given structure in the currently selected service
     *
     * @param deviceSchema  JSON string representing device schema
     * @param deviceId      The id to the device to be created
     * @param entityId      The id to the NGSI entity created representing the device
     * @param endpoint      The endpoint of the device to which actions will be sent on format IP:PORT
     * @param protocol      The protocol to be used on device. If no value is provided the default protocol (IoTA-UL) will be used
     * @return              Information of the registered device
     */
    public String registerDevice(String deviceSchema, String deviceId, String entityId, String endpoint, String protocol) throws JSONException {
        // logging.info("Registering device")

        String url = String.format("http://%s:%s/iot/devices?protocol=%s", idasHost, idasAdminPort, protocol);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");

        //Tests if file content is a valid JSON (or throws JSONException)
        new JSONObject(deviceSchema);

        String deviceSchemaReplaced = deviceSchema.replace("[DEVICE_ID]", deviceId)
                                                  .replace("[ENTITY_ID]", entityId);

        if (deviceSchemaReplaced.contains("\"endpoint\"")) {
            String[] endpointSplit = endpoint.split(":");
            String deviceIp = endpointSplit[0];
            String devicePort = endpointSplit[1];

            deviceSchemaReplaced = deviceSchemaReplaced.replace("[DEVICE_IP]", deviceIp)
                                                       .replace("[PORT]", devicePort);
        }

        return this.sendRequest(url, deviceSchemaReplaced, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Register a new device loading its structure from a given file
     *
     * @param deviceFilePath  The path to the description file for the device
     * @param deviceId        The id to the device to be created
     * @param entityId        The id to the NGSI entity created representing the device
     * @param endpoint        The endpoint of the device to which actions will be sent on format IP:PORT
     * @param protocol        The protocol to be used on device. If no value is provided the default protocol (IoTA-UL) will be used
     * @return                Information of the registered device
     */
    public String registerDeviceFromFile(String deviceFilePath, String deviceId, String entityId, String endpoint, String protocol) throws IOException, JSONException {
        // logging.info("Opening file '{}'".format(device_file_path))

        byte[] jsonDeviceFileBytes = Files.readAllBytes(Paths.get(deviceFilePath));
        String deviceSchemaJsonStr = new String(jsonDeviceFileBytes, Charset.defaultCharset());

        //Tests if file content is a valid JSON (or throws JSONException)
        new JSONObject(deviceSchemaJsonStr);

        return this.registerDevice(deviceSchemaJsonStr, deviceId, entityId, endpoint, protocol);
    }

    /*
     * Updates a registered device with the given structure in the currently selected service
     *
     * @param deviceSchema  JSON string representing device schema
     * @param deviceId      The id to the device to be updated
     * @param entityId      The id to the NGSI entity that represents the device
     * @param endpoint      The endpoint of the device to which actions will be sent on format IP:PORT
     * @param protocol      The protocol to be used on device. If no value is provided the default protocol (IoTA-UL) will be used
     * @return              Information of the updated device
     */
    public void updateDevice(String deviceSchema, String deviceId, String entityId, String endpoint, String protocol) {
        // TODO Implement
    }

    /*
     * Removes a device with the given id in the currently selected service
     *
     * @param deviceId  The id to the device to be removed
     * @return          Response of the removal request
     */
    public String removeDevice(String deviceId) {
        String url = String.format("http://%s:%s/iot/devices/%s", idasHost, idasAdminPort, deviceId);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.DELETE, additionalHeaders);
    }

    /*
     * Get device information given its device id
     *
     * @param deviceId  The id of the device to be searched
     * @return          The information of the device found with the given id or None if no device was found with the id
     */
    public void getDeviceById(String deviceId) {
        // TODO Implement
    }

    /*
     * List the devices registered in the currently selected service
     *
     * @return  The list of devices registered in the service
     */
    public String listDevices() {
        // logging.info("Listing devices")

        String url = String.format("http://%s:%s/iot/devices", idasHost, idasAdminPort);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET, additionalHeaders);
    }

    /*
     * Auxiliary method to create a standardized string from measurements group dict
     *
     * @param groupMeasurements  A dict representing a group of measurements, where the keys are the attribute names and the values are the measurements values for each attribute
     * @return                   A string representing the measurement group
     */
    private static String joinGroupMeasurements(Map<String, String> measurementGroup) {
        List<String> payloads = new ArrayList<>();
        measurementGroup.forEach((k, v) -> payloads.add(String.format("%s|%s", k, v)));
        return String.join("|", payloads);
    }

    /*
     * Auxiliary method to create a UL formatted payload string from a single measurement group to the FIWARE platform from a device
     *
     * @param measurements  A measurement group (a map where keys are device attributes and values are measurements for each attribute)
     * @return              A string containing the UL payload
     */
    protected static String createULPayloadFromMeasurementGroup(Map<String, String> measurementGroup) {
        //Single measurement group dict
        return FiwareIotClient.joinGroupMeasurements(measurementGroup);
    }

    /*
     * Auxiliary method to create a UL formatted payload string from a list of measurement groups to the FIWARE platform from a device
     *
     * @param measurements  A list of measurement groups obtained in the device
     * @return              A string containing the UL payload
     */
    private static String createULPayloadFromMeasurementGroupList(List<Map<String, String>> measurementGroups) {
        //Multiple measurement groups list
        List<String> groupsPayload = new ArrayList<>();
        measurementGroups.forEach(g -> groupsPayload.add(FiwareIotClient.joinGroupMeasurements(g)));

        return String.join("#", groupsPayload);
    }

    /*
     * Sends a list of measurement groups from a device to the FIWARE platform
     *
     * @param deviceId           The id of the device in which the measurement was obtained
     * @param measurementGroups  A measurement group list (a list of maps where keys are device attributes and values are measurements for each attribute) or a list of measurement groups obtained in the device
     * @param protocol           The transport protocol to be used to send measurements.
     *                           Currently accepted values are 'MQTT' and 'HTTP'. If no value is provided the default value (MQTT) will be used
     * @return                   The summary of the sent measurements
     */
    public String sendObservation(String deviceId, List<Map<String, String>> measurementGroups, String protocol) {
        System.out.println("Sending observation");

        String payload = createULPayloadFromMeasurementGroupList(measurementGroups);

        JSONObject resultJSON = new JSONObject();

        switch (protocol) {
            case "MQTT":
                System.out.println("Transport protocol: MQTT");
                String topic = String.format("/%s/%s/attrs", this.apiKey, deviceId);

                System.out.println(String.format("Publishing to %s on topic %s", this.idasHost, topic));
                System.out.print("Sending payload: ");
                System.out.println(payload);

                try {
                    this.publishMQTTMessage(topic, payload);
                    resultJSON.put("result", "OK");
                } catch (MqttException e) {
                    resultJSON.put("result", "ERROR");
                    resultJSON.put("error", e.getMessage());
                }
                break;

            case "HTTP":
                System.out.println("Transport protocol: UL-HTTP");
                String url = String.format("http://%s:%s/iot/d?k=%s&i=%s", this.idasHost, this.idasUL20Port, this.apiKey, deviceId);

                Map<String, String> additional_headers = new HashMap<>();
                additional_headers.put("Content-Type", "text/plain");

                this.sendRequest(url, payload, SimpleClient.POST, additional_headers);
                resultJSON.put("result", "OK");
                break;

            default:
                System.out.println(String.format("Unknown transport protocol '{}'", protocol));
                String errorMsg = "Unknown transport protocol. Accepted values are 'MQTT' and 'HTTP'";
                resultJSON.put("error", errorMsg);
                break;
        }

        return resultJSON.toString();
    }

    private void publishMQTTMessage(String topic, String payload) throws MqttException {
        MqttClient client = new MqttClient(String.format("tcp://%s:%s", this.mosquittoHost, this.mosquittoPort), MqttClient.generateClientId());
        client.connect();

        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());

        client.publish(topic, message);
        client.disconnectForcibly();
        client.close(true);
    }

    /*
     * Sends measurement group attributes from a device to the FIWARE platform
     *
     * @param deviceId      The id of the device in which the measurement was obtained
     * @param measurements  A measurement group (a map where keys are device attributes and values are measurements for each attribute)
     * @param protocol      The transport protocol to be used to send measurements.
     *                      Currently accepted values are 'MQTT' and 'HTTP'. If no value is provided the default value (MQTT) will be used
     * @return              The summary of the sent measurements
     */
    public String sendObservation(String deviceId, Map<String, String> measurements, String protocol) {
        return this.sendObservation(deviceId, Collections.singletonList(measurements), protocol);
    }

    /*
     * Sends a command from the FIWARE platform to a specific device (http://fiware-orion.readthedocs.io/en/latest/user/walkthrough_apiv1/index.html#ngsi10-standard-operations at "Update context elements" section)
     *
     * @param entityId  The id of the entity that represents the device
     * @param deviceId  The id of the device to which the command will be sent
     * @param command   The name of the command to be called on the device
     * @param params    The command parameters to be sent
     * @return          The result of the command call
     */
    public String sendCommand(String entityId, String deviceId, String command, List<String> params) {
        if(params == null) {
                params = new ArrayList<String>();
        }

        String url = String.format("http://%s:%s/v1/updateContext", this.idasHost, this.idasAdminPort);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");
        additionalHeaders.put("Accept", "application/json");

        String strParams = String.join("|", params);
        String value = strParams;

        String payload = String.format(
                            "{" +
                                  "\"contextElements\": [" +
                                    "{" +
                                        "\"type\": \"thing\"," +
                                        "\"isPattern\": \"false\"," +
                                        "\"id\": \"%s\"," +
                                        "\"attributes\": [" +
                                                "{" +
                                                    "\"name\": \"%s\"," +
                                                    "\"type\": \"command\"," +
                                                    "\"value\": \"%s\"" +
                                                "}" +
                                            "]" +
                                        "}" +
                                      "]," +
                                      "\"updateAction\": \"UPDATE\"" +
                        "}", entityId, command, value);


        return sendRequest(url, payload, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Get a list of polling commands of the device with the given id when sending a list of measurement groups to the FIWARE platform from a device with POST request
     *
     * @param deviceId      The id of the device to verify pooling commands
     * @param measurements  A measurement group (a dict where keys are device attributes and values are measurements for each attribute) or a list of measurement groups obtained in the device
     * @return              The list of pooling commands of the device
     */
    public String getPollingCommands(String deviceId, List<Map<String, String>> measurementGroups) {
        // logging.info("Sending measurement and getting pooling commands")

        String url = String.format("http://%s:%s/iot/d?k=%s&i=%s&getCmd=1", idasHost, idasUL20Port, apiKey, deviceId);
        String payload = createULPayloadFromMeasurementGroupList(measurementGroups);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "text/plain");

        return this.sendRequest(url, payload, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Get a list of polling commands of the device with the given id when sending a measurement group to the FIWARE platform from a device with POST request
     *
     * @param deviceId      The id of the device to verify pooling commands
     * @param measurements  A measurement group (a map where keys are device attributes and values are measurements for each attribute)
     * @return              The list of pooling commands of the device
     */
    public String getPollingCommands(String deviceId, Map<String, String> measurements) {
        return this.getPollingCommands(deviceId, Collections.singletonList(measurements));
    }

    public String getIdasHost() {
        return idasHost;
    }

    public void setIdasHost(String idasHost) {
        this.idasHost = idasHost;
    }

    public String getIdasAdminPort() {
        return idasAdminPort;
    }

    public void setIdasAdminPort(String idasAdminPort) {
        this.idasAdminPort = idasAdminPort;
    }

    public String getIdasUL20Port() {
        return idasUL20Port;
    }

    public void setIdasUL20Port(String idasUL20Port) {
        this.idasUL20Port = idasUL20Port;
    }

    public String getMosquittoHost() {
        return mosquittoHost;
    }

    public void setMosquittoHost(String mosquittoHost) {
        this.mosquittoHost = mosquittoHost;
    }

    public String getMosquittoPort() {
        return mosquittoPort;
    }

    public void setMosquittoPort(String mosquittoPort) {
        this.mosquittoPort = mosquittoPort;
    }

    public String getApiKey() {
        return apiKey;
    }

    /*
     * Sets the api key to use to send measurements from device
     *
     * @param apiKey  The api key of the service to use
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
