package br.ufrn.imd.fiotclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ini4j.InvalidFileFormatException;
import org.json.JSONObject;

import br.ufrn.imd.fiotclient.utils.ConfigParser;

/*
 * Default client for making requests to FIWARE APIs
 * 
 */
public class SimpleClient {
    
    private String fiwareService;
    private String fiwareServicePath;

    private String cbHost;
    private String cbPort;

    private String idasAAA;
    private String token;
    private String expiresAt;

    private String hostId;

    public static final String GET    = "GET";
    public static final String POST   = "POST";
    public static final String PUT    = "PUT";
    public static final String DELETE = "DELETE";

    private static final List<String> SUPPORTED_HTTP_METHODS = Arrays.asList(GET, POST, PUT, DELETE);

    /*
     * @param configFile  The file in which load the default configuration
     */
    public SimpleClient(String configFile) throws InvalidFileFormatException, IOException {
        Map<String, String> configMap = ConfigParser.readConfigFile(configFile);

        this.fiwareService = configMap.get("fiware_service");
        this.fiwareServicePath = configMap.get("fiware_service_path");

        this.cbHost = configMap.get("cb_host");
        this.cbPort = configMap.get("cb_port");

        this.idasAAA = configMap.get("idas_aaa");

        this.token = configMap.get("token");
        this.expiresAt = ""; //TODO Change

        this.hostId = configMap.get("host_id");
    }

    /*
     * Auxiliary method to configure and execute a request to FIWARE APIs
     *
     * @param url                The url to be called on the request
     * @param payload            The payload to be sent on the request
     * @param method             The method to be used on the request
     * @param additionalHeaders  Additional http headers to be used in the request
     * @return                   The response from the request execution
     */
    public String sendRequest(String url, String payload, String method, Map<String, String> additionalHeaders) {
        if (SUPPORTED_HTTP_METHODS.contains(method)) {
            JSONObject resultJSON = new JSONObject();
            try {
                System.out.print("Asking to:");
                System.out.println(url);

                HttpClient client = HttpClientBuilder.create().build();
                HttpUriRequest request;

                System.out.print("Method:");
                if (method.equals(SimpleClient.GET)) {
                    System.out.println("GET");
                    request = new HttpGet(url);
                } else if (method.equals(SimpleClient.POST)) {
                    System.out.println("POST");
                    request = new HttpPost(url);
                    ((HttpPost) request).setEntity(new StringEntity(payload));
                } else if (method.equals(SimpleClient.PUT)) {
                    System.out.println("PUT");
                    request = new HttpPut(url);
                    ((HttpPut) request).setEntity(new StringEntity(payload));
                } else { //if (method.equals(SimpleClient.DELETE)) {
                    System.out.println("DELETE");
                    request = new HttpDelete(url);
                }

                if(!payload.equals("")) {
                    System.out.print("Sending payload:");
                    System.out.println(payload);
                }

                request.addHeader("X-Auth-Token", this.getToken());
                request.addHeader("Fiware-Service", this.getFiwareService());
                request.addHeader("Fiware-ServicePath", this.getFiwareServicePath());

                if(additionalHeaders.size() > 0) {
                    additionalHeaders.forEach((k, v) -> request.addHeader(k, v));
                }

                System.out.println(String.format("Asking to {}", url));
                System.out.print("Headers:");
                System.out.println(request.getAllHeaders().toString());

                //TODO Adds timeout or verifications of servers on calls to APIs

                HttpResponse response = client.execute(request);

                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println(String.format("Status Code: {}", statusCode));

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                String strResponse = result.toString();
                System.out.print("Response: ");
                System.out.println(strResponse);

                resultJSON.put("status_code", statusCode);
                resultJSON.put("response", strResponse);
            } catch (UnsupportedOperationException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                return resultJSON.toString();
            }
        } else {
            //logging.error("Unsupported method '{}'".format(str(method)))
                String errorMsg = String.format("Unsupported method '{}'. Select one of 'GET', 'POST', 'PUT' and 'DELETE'", method);
                JSONObject resultJSON = new JSONObject();
                resultJSON.put("error", errorMsg);
                return resultJSON.toString();
        }
    }

    /*
     * Auxiliary method to configure and execute a request to FIWARE APIs without additional headers
     *
     * @param url                The url to be called on the request
     * @param payload            The payload to be sent on the request
     * @param method             The method to be used on the request
     * @return                   The response from the request execution
     */
    public String sendRequest(String url, String payload, String method) {
        return this.sendRequest(url, payload, method, new HashMap<String, String>());
    }

    /*
     * Generates an authentication token based on user credentials using FIWARE Lab OAuth2.0 Authentication system. If you didn't have a user, go and register first at http://cloud.fiware.org
     *
     * @param username  The user's username from Fiware authentication account
     * @param password  The user's password from Fiware authentication account
     * @return          The generated token and expiration
     */
    private void authenticate(String username, String password) {
//	        logging.info('Generating token')
//
//	        tokens_url = "http://cloud.lab.fi-ware.org:4730/v2.0/tokens"
//
//	        payload = {
//	            "auth": {
//	                "passwordCredentials": {
//	                    "username": str(username),
//	                    "password": str(password)
//	                }
//	            }
//	        }
//
//	        headers = {'Content-Type': 'application/json'}
//	        url = tokens_url
//
//	        resp = requests.post(url, data=json.dumps(payload), headers=headers)
//
//	        self.token = resp.json()["access"]["token"]["id"]
//	        self.expires_at = resp.json()["access"]["token"]["expires"]
//
//	        logging.debug("FIWARE OAuth2.0 Token: {}".format(self.token))
//	        logging.debug("Token expiration: {}".format(self.expires_at))
    }

    /*
     * Specify the service context to use on operations
     *
     * @param fiwareService      The name of the service to be used
     * @param fiwareServicePath  The service path of the service to be used
     */
    public void setService(String fiwareService, String fiwareServicePath) {
        this.fiwareService = fiwareService;
        this.fiwareServicePath = fiwareServicePath;
    }

    public String getFiwareService() {
        return fiwareService;
    }

    public void setFiwareService(String fiwareService) {
        this.fiwareService = fiwareService;
    }

    public String getFiwareServicePath() {
        return fiwareServicePath;
    }

    public void setFiwareServicePath(String fiwareServicePath) {
        this.fiwareServicePath = fiwareServicePath;
    }

    public String getCbHost() {
        return cbHost;
    }

    public void setCbHost(String cbHost) {
        this.cbHost = cbHost;
    }

    public String getCbPort() {
        return cbPort;
    }

    public void setCbPort(String cbPort) {
        this.cbPort = cbPort;
    }

    public String getIdasAAA() {
        return idasAAA;
    }

    public void setIdasAAA(String idasAAA) {
        this.idasAAA = idasAAA;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

}
