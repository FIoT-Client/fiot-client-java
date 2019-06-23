package br.ufrn.imd.fiotclient.utils;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigParser {

    /*
     * Load configuration file and creates a map with the necessary attributes
     *
     * @param configFile  The file to be read
     * @return            A map with the attributes read from the file
     */
    public static Map<String, String> readConfigFile(String configFile) throws IOException {
        Wini ini = new Wini(new File(configFile));

        Map<String, String> configMap = new HashMap<>();
        configMap.put("fiware_service", ini.get("service", "fiware_service"));
        configMap.put("fiware_service_path", ini.get("service", "fiware_service_path"));

        configMap.put("context_broker_host", ini.get("context_broker", "host"));
        configMap.put("context_broker_port", ini.get("context_broker", "port"));

        configMap.put("iota_host", ini.get("iota", "host"));
        configMap.put("iota_north_port", ini.get("iota", "north_port"));
        configMap.put("iota_protocol_port", ini.get("iota", "protocol_port"));
        configMap.put("iota_aaa", ini.get("iota", "oauth"));
        configMap.put("iota_api_key", ini.get("iota", "api_key"));

        configMap.put("mqtt_broker_host", ini.get("mqtt_broker", "host"));
        configMap.put("mqtt_broker_port", ini.get("mqtt_broker", "port"));

        configMap.put("sth_host", ini.get("sth_comet", "host"));
        configMap.put("sth_port", ini.get("sth_comet", "port"));

        configMap.put("cygnus_host", ini.get("cygnus", "host"));
        configMap.put("cygnus_port", ini.get("cygnus", "port"));
        configMap.put("cygnus_notification_host", ini.get("cygnus", "notification_host"));

        configMap.put("perseo_host", ini.get("perseo", "host"));
        configMap.put("perseo_port", ini.get("perseo", "port"));

        if (configMap.get("iota_aaa").equals("yes")) {
            configMap.put("token", ini.get("user", "token"));
            configMap.put("token_show", "*****");
        } else {
            configMap.put("token", "NULL");
            configMap.put("token_show", "NULL");
        }

        configMap.put("host_type", ini.get("local", "host_type"));
        configMap.put("host_id", ini.get("local", "host_id"));

        return configMap;
    }

}
