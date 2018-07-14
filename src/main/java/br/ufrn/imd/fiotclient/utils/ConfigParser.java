package br.ufrn.imd.fiotclient.utils;

import org.ini4j.InvalidFileFormatException;
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
    public static Map<String, String> readConfigFile(String configFile) throws InvalidFileFormatException, IOException {
        Wini ini = new Wini(new File(configFile));

        Map<String, String> configMap = new HashMap<>();
        configMap.put("fiware_service", ini.get("service", "fiware-service"));
        configMap.put("fiware_service_path", ini.get("service", "fiware-service-path"));

        configMap.put("cb_host", ini.get("contextbroker", "host"));
        configMap.put("cb_port", ini.get("contextbroker", "port"));

        configMap.put("idas_aaa", ini.get("idas", "OAuth"));
        configMap.put("idas_host", ini.get("idas", "host"));
        configMap.put("idas_admin_port", ini.get("idas", "adminport"));
        configMap.put("idas_ul20_port", ini.get("idas", "ul20port"));
        configMap.put("api_key", ini.get("idas", "apikey"));

        configMap.put("mosquitto_host", ini.get("mosquitto", "host"));
        configMap.put("mosquitto_port", ini.get("mosquitto", "port"));

        configMap.put("sth_host", ini.get("sthcomet", "host"));
        configMap.put("sth_port", ini.get("sthcomet", "port"));

        configMap.put("cygnus_host", ini.get("cygnus", "host"));
        configMap.put("cygnus_notification_host", ini.get("cygnus", "notification_host"));
        configMap.put("cygnus_port", ini.get("cygnus", "port"));

        configMap.put("perseo_host", ini.get("perseo", "host"));
        configMap.put("perseo_port", ini.get("perseo", "port"));

        if (configMap.get("idas_aaa").equals("yes")) {
            configMap.put("token", ini.get("user", "token"));
            configMap.put("token_show", "*****");
        } else {
            configMap.put("token", "NULL");
            configMap.put("token_show", "NULL");
        }

        configMap.put("host_id", ini.get("local", "host_id"));

        return configMap;
    }

}
