package br.ufrn.imd.fiotclient.context;

import br.ufrn.imd.fiotclient.SimpleClient;
import br.ufrn.imd.fiotclient.utils.ConfigParser;
import org.ini4j.InvalidFileFormatException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import logging

//__copyright__ = "Copyright 2017, Lucas Cristiano Calixto Dantas"
//__credits__ = ["Lucas Cristiano Calixto Dantas"]
//__license__ = "MIT"
//__version__ = "0.1.0"
//__maintainer__ = "Lucas Cristiano Calixto Dantas"
//__email__ = "lucascristiano27@gmail.com"
//__status__ = "Development"

/*
 * Client for doing context management operations on FIWARE platform
 * 
 * @author Lucas Cristiano Calixto Dantas
 */
public class FiwareContextClient extends SimpleClient {

    private String sthHost;
    private String sthPort;

    private String cygnusHost;
    private String cygnusNotificationHost;
    private String cygnusPort;

    private String perseoHost;
    private String perseoPort;

    /*
     * @param configFile  The file in which load the default configuration
     */
    public FiwareContextClient(String configFile) throws InvalidFileFormatException, IOException {
        super(configFile);
        
        Map<String, String> configMap = ConfigParser.readConfigFile(configFile);
        
        this.sthHost = configMap.get("sth_host");
        this.sthPort = configMap.get("sth_port");

        this.cygnusHost = configMap.get("cygnus_host");
        this.cygnusNotificationHost = configMap.get("cygnus_notification_host");
        this.cygnusPort = configMap.get("cygnus_port");

        this.perseoHost = configMap.get("perseo_host");
        this.perseoPort = configMap.get("perseo_port");
    }

    /*
     * Creates a new NGSI entity with the given structure in the currently selected service
     *
     * @param entitySchema  JSON string representing entity schema
     * @param entityId      The id to the entity to be created
     * @return              Information of the registered entity
     */
    public String createEntity(String entitySchema, String entityId) {
        String url = String.format("http://%s:%s/v2/entities", this.getCbHost(), this.getCbPort());

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");

        //Tests if file content is a valid JSON (or throws JSONException)
        new JSONObject(entitySchema);

        String entitySchemaReplaced = entitySchema.replace("[ENTITY_ID]", entityId);

        return this.sendRequest(url, entitySchemaReplaced, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Creates a new NGSI entity loading its structure from a given file
     *
     * @param entityFilePath  The path to the description file for the entity
     * @param entityId        The id to the entity to be created
     * @return                Information of the registered entity
     */
    public String createEntityFromFile(String entityFilePath, String entityId) throws IOException {
        // logging.info("Opening file '{}'".format(entity_file_path))

        byte[] jsonEntityFileBytes = Files.readAllBytes(Paths.get(entityFilePath));
        String entitySchemaJsonStr = new String(jsonEntityFileBytes, Charset.defaultCharset());

        //Tests if file content is a valid JSON (or throws JSONException)
        new JSONObject(entitySchemaJsonStr);

        return this.createEntity(entitySchemaJsonStr, entityId);
    }

    /*
     * Updates an entity with the given id for the new structure in the currently selected service
     * 
     * @param entityId      The id to the entity to be updated
     * @param entitySchema  JSON string representing new entity schema
     * @return              Information of the updated entity
     */
    public void updateEntity(String entityId, String entitySchema) {
        //TODO Implement
    }

    /*
     * Removes an entity with the given id
     * 
     * @param entityId    The id to the entity to be removed
     * @param entityType  The type of the entity to be removed
     * @return            Information of the removed entity
     */
    public String removeEntity(String entityId, String entityType) {
//        logging.info("Removing entity with id '{}'".format(entity_id))

        String url = String.format("http://%s:%s/v2/entities/%s?type=%s", this.getCbHost(), this.getCbPort(), entityId, entityType);
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.DELETE);
    }

    /*
     * Get entity information given its entity id
     * 
     * @param entityId    The id of the entity to be searched
     * @param entityType  The type of the entity to be searched
     * @return            The information of the entity found with the given id or None if no entity was found with the id
     */
    public String getEntityById(String entityId, String entityType) {
//        logging.info("Getting entity by id '{}'".format(entity_id))

        String url = String.format("http://%s:%s/v2/entities/%s?type=%s", this.getCbHost(), this.getCbPort(), entityId, entityType);
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    /*
     * Get entities created with a given entity type
     * 
     * @param entityType  The type of the entities to be searched
     * @return            A list with the information of the entities found with the given type
     */
    public String getEntitiesByType(String entityType) {
//        logging.info("Getting entities by type '{}'".format(type))

        String url = String.format("http://%s:%s/v2/entities?type=%s", getCbHost(), getCbPort(), entityType);
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    /*
     * Get all created entities
     *
     * @return  A list with the information of all the created entities
     */
    public String getEntities() {
//        logging.info("Getting all entities")

        String url = String.format("http://%s:%s/v2/entities", getCbHost(), getCbPort());
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    /*
     * Create a new subscription on given attributes of the device with the specified id
     * 
     * @param deviceId         The id of the device to be monitored
     * @param attributes       The list of attributes do be monitored
     * @param notificationUrl  The URL to which the notification will be sent on changes
     * @return                 The information of the subscription
     */
    public String subscribeAttributesChange(String deviceId, List<String> attributes, String notificationUrl) {
//        logging.info("Subscribing for change on attributes '{}' on device with id '{}'".format(attributes, device_id))

        String url = String.format("http://%s:%s/v1/subscribeContext", this.getCbHost(), this.getCbPort());

        HashMap<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Accept", "application/json");
        additionalHeaders.put("Content-Type", "application/json");

        String attributesStr = attributes.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
        attributesStr = String.format("[%s]", attributesStr);

        String payload = String.format(
                            "{" +
                                "\"entities\": [" +
                                    "{" +
                                        "\"type\": \"thing\"," +
                                        "\"isPattern\": \"false\"," +
                                        "\"id\": \"%s\"" +
                                    "}" +
                                "]," +
                                "\"attributes\": \"%s\"," +
                                "\"notifyConditions\": [" +
                                    "{" +
                                        "\"type\": \"ONCHANGE\"," +
                                        "\"condValues\": \"%s\"" +
                                    "}" +
                                "]," +
                                "\"reference\": \"%s\"," +
                                "\"duration\": \"P1Y\"," +
                                "\"throttling\": \"PT1S\"" +
                            "}", deviceId, attributesStr, attributesStr, notificationUrl);

        return this.sendRequest(url, payload, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Register a new rule to be evaluated on attribute values change and a action to be taken when rule evaluated to true
     * 
     * @param attribute        The attribute to be monitored
     * @param attributeType    The type of the attribute to be monitored
     * @param condition        The condition to be evaluated on changes on attribute's value
     * @param action           The action type to be taken when condition is evaluated true.
     *                         Currently accepted values to this parameter are 'email' and 'post'
     * @param notificationUrl  The endpoint to which POST notifications will be sent
     * @return                 The information of the created rule
     */
    public String subscribeAttributeChangeWithRule(String attribute, String attributeType, String condition, String action, String notificationUrl) {
//        logging.info("Creating attribute change rule")

        String url = String.format("http://%s:%s/rules", perseoHost, perseoPort);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Content-Type", "application/json");
        additionalHeaders.put("Accept", "application/json");

        String ruleTemplate = "select *,\"%s-rule\" as ruleName from pattern " +
                        "[every ev=iotEvent(cast(cast(ev.%s?,String),%s)%s)]";

        String payload = String.format(
                            "{" +
                                "\"name\": \"%s-rule\"," +
                                "\"text\": " + ruleTemplate + "," +
                                "\"action\": {" +
                                    "\"type\": \"\"," +
                                    "\"template\": " + String.format("Alert! %s is now ${{ev.%s}}.", attribute, attribute) + "," +
                                    "\"parameters\": {}" +
                                "}" +
                            "}", attribute, attribute, attribute, attributeType, condition);

        JSONObject payloadObject = new JSONObject(payload);

        switch (action) {
            case "email": {
                payloadObject = payloadObject.getJSONObject("action").put("type", "email");

                //TODO Remove hardcoded info
                JSONObject parametersObject = new JSONObject();
                parametersObject.put("to", "lucascristiano27@gmail.com");
                parametersObject.put("from", "lucas.calixto.dantas@gmail.com");
                parametersObject.put("subject", String.format("Alert! High %s Detected", attribute.toUpperCase()));
                payloadObject = payloadObject.getJSONObject("action").put("parameters", parametersObject);

                break;
            }

            case "post": {
                payloadObject = payloadObject.getJSONObject("action").put("type", "post");

                //TODO Remove hardcoded info
                JSONObject parametersObject = new JSONObject();
                parametersObject.put("url", notificationUrl);
                payloadObject = payloadObject.getJSONObject("action").put("parameters", parametersObject);
                break;
            }

            default:
                String errorMsg = String.format("Unknown action '%s'", action);
//            logging.error(error_msg)
                return String.format("{'error': '%s'}", errorMsg);
        }

        return this.sendRequest(url, payloadObject.toString(), SimpleClient.POST, additionalHeaders);
    }

    /*
     * Create a new subscription on attributes to send changes on its values to sinks configured on Cygnus
     * 
     * @param entityId   The id of the entity to be monitored
     * @param attributes The list of attributes do be monitored
     * @return           The information of the subscription
     */
    public String subscribeCygnus(String entityId, List<String> attributes) {
//        logging.info("Subscribing Cygnus")

        String notificationUrl = String.format("http://%s:%s/notify", cygnusNotificationHost, cygnusPort);
        return this.subscribeAttributesChange(entityId, attributes, notificationUrl);
    }

    /*
     * Create a new subscription on attributes to store changes on its values as historical data
     * 
     * @param entityId    The id of the entity to be monitored
     * @param attributes  The list of attributes do be monitored
     * @return            The information of the subscription
     */
    public String subscribeHistoricalData(String entityId, List<String> attributes) {
//        logging.info("Subscribing to historical data")

        String notificationUrl = String.format("http://%s:%s/notify", sthHost, sthPort);
        return this.subscribeAttributesChange(entityId, attributes, notificationUrl);
    }

    /*
     * Get historical data from a specific attribute of an entity
     * 
     * @param entityType   The type of the entity to get historical data
     * @param entityId     The id of the entity to get historical data
     * @param attribute    The attribute of the entity to get historical data
     * @param itemsNumber  The number of last entries to be queried.
     *                     If no value is provided, the default value (10 entries) will be used
     * @return             The historical data on the specified attribute of the given entity
     */
    public String getHistoricalData(String entityType, String entityId, String attribute, int itemsNumber) {
//        logging.info("Getting historical data")

        String url = String.format("http://%s:%s/STH/v1/contextEntities/type/%s/id/%s/attributes/%s?lastN=%s",
                        sthHost, sthPort, entityType, entityId, attribute, itemsNumber);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Accept", "application/json");
        additionalHeaders.put("Fiware-Service", getFiwareService().toLowerCase());
        additionalHeaders.put("Fiware-ServicePath", getFiwareServicePath().toLowerCase());

        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET, additionalHeaders);
    }

    /*
     * Remove a subscription with the given subscription id
     * 
     * @param subscriptionId  The id of the subscription to be removed
     * @return                True if the subscription with the given id was removed
     *                        False if no subscription with the given id was removed
     */
    public String unsubscribe(String subscriptionId) {
//        logging.info("Removing subscriptions")

        String url = String.format("http://%s:%s/v1/unsubscribeContext", getCbHost(), getCbPort());

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Accept", "application/json");
        additionalHeaders.put("Content-Type", "application/json");

        String payload = String.format("{\"subscriptionId\": \"%s\"}", subscriptionId);

        return this.sendRequest(url, payload, SimpleClient.POST, additionalHeaders);
    }

    /*
     * Get subscription information given its subscription id
     * 
     * @param subscriptionId  The id of the subscription to be searched
     * @return                The information of the subscription found with the given id or None if no subscription was found with the id
     */
    public String getSubscriptionById(String subscriptionId) {
//        logging.info("Getting subscription by id '{}'".format(subscription_id))

        String url = String.format("http://%s:%s/v2/subscriptions/%s", getCbHost(), getCbPort(), subscriptionId);
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    /*
     * Get all subscriptions
     * 
     * @return  A list with the ids of all the subscriptions
     */
    public String listSubscriptions() {
//        logging.info("Listing subscriptions")

        String url = String.format("http://%s:%s/v2/subscriptions", getCbHost(), getCbPort());
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    public String getSthHost() {
        return sthHost;
    }

    public void setSthHost(String sthHost) {
        this.sthHost = sthHost;
    }

    public String getSthPort() {
        return sthPort;
    }

    public void setSthPort(String sthPort) {
        this.sthPort = sthPort;
    }

    public String getCygnusHost() {
        return cygnusHost;
    }

    public void setCygnusHost(String cygnusHost) {
        this.cygnusHost = cygnusHost;
    }

    public String getCygnusNotificationHost() {
        return cygnusNotificationHost;
    }

    public void setCygnusNotificationHost(String cygnusNotificationHost) {
        this.cygnusNotificationHost = cygnusNotificationHost;
    }

    public String getCygnusPort() {
        return cygnusPort;
    }

    public void setCygnusPort(String cygnusPort) {
        this.cygnusPort = cygnusPort;
    }

    public String getPerseoHost() {
        return perseoHost;
    }

    public void setPerseoHost(String perseoHost) {
        this.perseoHost = perseoHost;
    }

    public String getPerseoPort() {
        return perseoPort;
    }

    public void setPerseoPort(String perseoPort) {
        this.perseoPort = perseoPort;
    }

}
