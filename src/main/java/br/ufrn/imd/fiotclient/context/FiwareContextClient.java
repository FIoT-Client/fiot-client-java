package br.ufrn.imd.fiotclient.context;

import java.io.IOException;
import java.util.Map;

import org.ini4j.InvalidFileFormatException;

import br.ufrn.imd.fiotclient.SimpleClient;
import br.ufrn.imd.fiotclient.utils.ConfigParser;

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
     * @param entityId      The id to the entity to be created
     * @param entitySchema  JSON string representing entity schema
     * @return              Information of the registered entity
     */
    public void createEntity(String entityId, String entitySchema) {
        //TODO Implement
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
     * @param entityId  The id to the entity to be removed
     * @return          Information of the removed entity
     */
    public void removeEntity(String entityId) {
    		//TODO Implement
    }

    /*
     * Get entity information given its entity id
     * 
     * @param entityId  The id of the entity to be searched
     * @return          The information of the entity found with the given id or None if no entity was found with the id
     */
    public String getEntityById(String entityId) {
        System.out.println(String.format("Getting entity by id '%s'", entityId));
        
        //TODO Remove hardcoded type from url
        String url = String.format("http://%s:%s/v2/entities/%s/attrs?type=thing", this.getCbHost(), this.getCbPort(), entityId);
        String payload = "";

        return this.sendRequest(url, payload, SimpleClient.GET);
    }

    //TODO Implement get all entities of all types of selected service

    /*
     * Get entities created with a given entity type
     * 
     * @param entityType  The type of the entities to be searched
     * @return            A list with the information of the entities found with the given type
     */
    public void getEntitiesByType(String entityType) {
//        logging.info("Getting entities by type '{}'".format(type))
//
//        url = "http://{}:{}/v2/entities?type={}".format(self.cb_host, self.cb_port, entity_type)
//        payload = ''
//
//        return self._send_request(url, payload, 'GET')
    }

    /*
     * Create a new subscription on given attributes of the device with the specified id
     * 
     * @param deviceId         The id of the device to be monitored
     * @param attributes       The list of attributes do be monitored
     * @param notificationUrl  The URL to which the notification will be sent on changes
     * @return                 The information of the subscription
     */
    public void subscribeAttributesChange(String deviceId, String attributes, String notificationUrl) {
//        logging.info("Subscribing for change on attributes '{}' on device with id '{}'".format(attributes, device_id))
//
//        url = "http://{}:{}/v1/subscribeContext".format(self.cb_host, self.cb_port)
//
//        additional_headers = {'Accept': 'application/json',
//                              'Content-Type': 'application/json'}
//
//        payload = {"entities": [{
//            "type": "thing",
//            "isPattern": "false",
//            "id": str(device_id)
//        }],
//            "attributes": attributes,
//            "notifyConditions": [{
//                "type": "ONCHANGE",
//                "condValues": attributes
//            }],
//            "reference": notification_url,
//            "duration": "P1Y",
//            "throttling": "PT1S"
//        }
//
//        return self._send_request(url, payload, 'POST', additional_headers=additional_headers)
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
    public void subscribeAttributeChangeWithRule(String attribute, String attributeType, String condition, String action, String notificationUrl) {
//        logging.info("Creating attribute change rule")
//
//        url = "http://{}:{}/rules".format(self.perseo_host, self.perseo_port)
//
//        additional_headers = {'Accept': 'application/json',
//                              'Content-Type': 'application/json'}
//
//        rule_template = "select *,\"{}-rule\" as ruleName from pattern " \
//                        "[every ev=iotEvent(cast(cast(ev.{}?,String),{}){})]"
//        payload = {
//            "name": "{}-rule".format(attribute),
//            "text": rule_template.format(attribute, attribute, attribute_type, condition),
//            "action": {
//                "type": "",
//                "template": "Alert! {0} is now ${{ev.{1}}}.".format(attribute, attribute),
//                "parameters": {}
//            }
//        }
//
//        if action == 'email':
//            payload["action"]["type"] = "email"
//            # TODO Remove hardcoded info
//            payload["action"]["parameters"] = {"to": "{}".format("lucascristiano27@gmail.com"),
//                                               "from": "{}".format("lucas.calixto.dantas@gmail.com"),
//                                               "subject": "Alert! High {} Detected".format(attribute.capitalize())}
//        elif action == 'post':
//            payload["action"]["type"] = "post"
//            payload["action"]["parameters"] = {"url": "{}".format(notification_url)}
//
//        else:
//            error_msg = "Unknown action '{}'".format(action)
//            logging.error(error_msg)
//            return {'error': error_msg}
//
//        return self._send_request(url, payload, 'POST', additional_headers=additional_headers)
    }

    /*
     * Create a new subscription on attributes to send changes on its values to sinks configured on Cygnus
     * 
     * @param entityId   The id of the entity to be monitored
     * @param attributes The list of attributes do be monitored
     * @return           The information of the subscription
     */
    public void subscribeCygnus(String entityId, String attributes) {
//        logging.info("Subscribing Cygnus")
//
//        notification_url = "http://{}:{}/notify".format(self.cygnus_notification_host, self.cygnus_port)
//        return self.subscribe_attributes_change(entity_id, attributes, notification_url)
    }

    /*
     * Create a new subscription on attributes to store changes on its values as historical data
     * 
     * @param entityId    The id of the entity to be monitored
     * @param attributes  The list of attributes do be monitored
     * @return            The information of the subscription
     */
    public void subscribeHistoricalData(String entityId, String attributes) {
//        logging.info("Subscribing to historical data")
//
//        notification_url = "http://{}:{}/notify".format(self.sth_host, self.sth_port)
//        return self.subscribe_attributes_change(entity_id, attributes, notification_url)
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
    public void getHistoricalData(String entityType, String entityId, String attribute, int items_number) {
//        logging.info("Getting historical data")
//
//        url = "http://{}:{}/STH/v1/contextEntities" \
//              "/type/{}/id/{}/attributes/{}?lastN={}".format(self.sth_host, self.sth_port, entity_type, entity_id,
//                                                             attribute, items_number)
//
//        additional_headers = {'Accept': 'application/json',
//                              'Fiware-Service': str(self.fiware_service).lower(),
//                              'Fiware-ServicePath': str(self.fiware_service_path).lower()}
//
//        payload = ''
//
//        return self._send_request(url, payload, 'GET', additional_headers=additional_headers)
    }

    /*
     * Remove a subscription with the given subscription id
     * 
     * @param subscriptionId  The id of the subscription to be removed
     * @return                True if the subscription with the given id was removed
     *                        False if no subscription with the given id was removed
     */
    public void unsubscribe(String subscriptionId) {
//        logging.info("Removing subscriptions")
//
//        url = "http://{}:{}/v1/unsubscribeContext".format(self.cb_host, self.cb_port)
//
//        additional_headers = {'Accept': 'application/json',
//                              'Content-Type': 'application/json'}
//
//        payload = {"subscriptionId": str(subscription_id)}
//
//        return self._send_request(url, payload, 'POST', additional_headers=additional_headers)
    }

    /*
     * Get subscription information given its subscription id
     * 
     * @param subscriptionId  The id of the subscription to be searched
     * @return                The information of the subscription found with the given id or None if no subscription was found with the id
     */
    public void getSubscriptionById(String subscriptionId) {
//        logging.info("Getting subscription by id '{}'".format(subscription_id))
//
//        url = "http://{}:{}/v2/subscriptions/{}".format(self.cb_host, self.cb_port, subscription_id)
//
//        payload = ''
//
//        return self._send_request(url, payload, 'GET')
    }

    /*
     * Get all subscriptions
     * 
     * @return  A list with the ids of all the subscriptions
     */
    public void listSubscriptions() {
//        logging.info("Listing subscriptions")
//
//        url = "http://{}:{}/v2/subscriptions".format(self.cb_host, self.cb_port)
//
//        payload = ''
//
//        return self._send_request(url, payload, 'GET')
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
