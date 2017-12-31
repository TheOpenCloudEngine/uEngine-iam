package org.opencloudengine.garuda.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.opencloudengine.garuda.client.model.OauthClient;
import org.opencloudengine.garuda.client.model.OauthScope;
import org.opencloudengine.garuda.client.model.OauthUser;
import org.opencloudengine.garuda.client.util.HttpUtils;
import org.opencloudengine.garuda.client.util.JsonUtils;
import org.opencloudengine.garuda.client.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by uengine on 2016. 9. 8..
 */
public class IamClient {

    /**
     * SLF4J Application Logging
     */
    private Logger logger = LoggerFactory.getLogger(IamClient.class);

    private String schema = "http";

    private String host;

    private Integer port;

    private String oauthBasePath = "/oauth";

    private String restBasePath = "/rest/v1";

    private String clientId;

    private String clientSecret;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getOauthBasePath() {
        return oauthBasePath;
    }

    public void setOauthBasePath(String oauthBasePath) {
        this.oauthBasePath = oauthBasePath;
    }

    public String getRestBasePath() {
        return restBasePath;
    }

    public void setRestBasePath(String restBasePath) {
        this.restBasePath = restBasePath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public IamClient(String host, int port, String clientId, String clientSecret) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public Map accessToken(AccessTokenRequest request) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(request);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        if (!params.containsKey("client_id")) {
            params.put("client_id", this.clientId);
        }
        if (!params.containsKey("client_secret")) {
            params.put("client_secret", this.clientSecret);
        }

        String queryString = HttpUtils.createPOSTQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createOauthBaseUrl() + "/access_token";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response = httpUtils.makeRequest("POST", url, queryString, headers);

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        unmarshal.put("status", response.getStatusLine().getStatusCode());
        return unmarshal;
    }

    public Map tokenInfo(String token) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", token);

        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createOauthBaseUrl() + "/token_info" + queryString;

        Map<String, String> headers = new HashMap();

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        unmarshal.put("status", response.getStatusLine().getStatusCode());
        return unmarshal;
    }

    public OauthUser createUser(OauthUser oauthUser) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthUser);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("POST", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth user");
        }
        Header[] locations = response.getHeaders("Location");
        Header location = locations[0];
        String userId = location.getValue().substring(location.getValue().lastIndexOf("/") + 1);

        return getUser(userId);
    }

    public List<OauthUser> getAllUser() throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get all oauth user");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        List<Map> list = JsonUtils.unmarshalToList(responseText);
        List<OauthUser> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : list) {
            result.add(objectMapper.convertValue(map, OauthUser.class));
        }
        return result;
    }

    public OauthUser getUser(String userId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user/" + userId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth user");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthUser.class);
    }

    public OauthUser getUserByName(String userName) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user/search/findByName?userName=" + userName;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth user");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthUser.class);
    }

    public OauthUser updateUser(OauthUser oauthUser) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthUser);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        String userId = oauthUser.get_id();

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user/" + userId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("PUT", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new Exception("Failed to update oauth user");
        }
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthUser.class);
    }

    public void deleteUser(String userId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user/" + userId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth user");
        }
    }

    public OauthUser createUserAvatar(File file, String contentType, String id, String userName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(id)) {
            params.put("id", id);
        }
        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }
        params.put("contentType", contentType);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeFileRequest("POST", url + queryString, file, headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth user avatar");
        }

        Header[] locations = response.getHeaders("Location");
        Header location = locations[0];
        String userId = location.getValue().substring(location.getValue().lastIndexOf("/") + 1);
        return getUser(userId);
    }

    public void deleteUserAvatar(String id, String userName) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar";
        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(id)) {
            params.put("id", id);
        }
        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }
        String queryString = HttpUtils.createGETQueryString(params);

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url + queryString, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth user avatar");
        }
    }

    public void getUserAvatar(String id, String userName, OutputStream outputStream) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar";
        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(id)) {
            params.put("id", id);
        }
        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }
        String queryString = HttpUtils.createGETQueryString(params);

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url + queryString, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth user avatar");
        }
        response.getEntity().writeTo(outputStream);
    }

    public OauthClient createClient(OauthClient oauthClient) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthClient);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("POST", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth client");
        }
        Header[] locations = response.getHeaders("Location");
        Header location = locations[0];
        String clientId = location.getValue().substring(location.getValue().lastIndexOf("/") + 1);

        return getClient(clientId);
    }

    public List<OauthClient> getAllClient() throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get all oauth client");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        List<Map> list = JsonUtils.unmarshalToList(responseText);
        List<OauthClient> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : list) {
            result.add(objectMapper.convertValue(map, OauthClient.class));
        }
        return result;
    }

    public OauthClient getClient(String clientId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth client");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthClient.class);
    }

    public OauthClient updateClient(OauthClient oauthClient) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthClient);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        String clientId = oauthClient.get_id();

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("PUT", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new Exception("Failed to update oauth client");
        }
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthClient.class);
    }

    public void deleteClient(String clientId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth client");
        }
    }

    public void createClientScope(String clientId, String scopeId) throws Exception {
        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("POST", url, "", headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth client scope");
        }
    }

    public List<OauthScope> getAllClientScope(String clientId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId + "/scope";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get all oauth client scope");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        List<Map> list = JsonUtils.unmarshalToList(responseText);
        List<OauthScope> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : list) {
            result.add(objectMapper.convertValue(map, OauthScope.class));
        }
        return result;
    }

    public OauthScope getClientScope(String clientId, String scopeId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth client scope");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthScope.class);
    }

    public void deleteClientScope(String clientId, String scopeId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientId + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth client scope");
        }
    }

    public OauthScope createScope(OauthScope oauthScope) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthScope);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("POST", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth scope");
        }
        Header[] locations = response.getHeaders("Location");
        Header location = locations[0];
        String scopeId = location.getValue().substring(location.getValue().lastIndexOf("/") + 1);

        return getScope(scopeId);
    }

    public List<OauthScope> getAllScope() throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope";

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get all oauth scope");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        List<Map> list = JsonUtils.unmarshalToList(responseText);
        List<OauthScope> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map map : list) {
            result.add(objectMapper.convertValue(map, OauthScope.class));
        }
        return result;
    }

    public OauthScope getScope(String scopeId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth scope");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthScope.class);
    }

    public OauthScope updateScope(OauthScope oauthScope) throws Exception {
        Map<String, Object> map = JsonUtils.convertClassToMap(oauthScope);
        Map<String, Object> params = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                params.put(key, map.get(key));
            }
        }

        String scopeId = oauthScope.get_id();

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("PUT", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new Exception("Failed to update oauth scope");
        }
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(unmarshal, OauthScope.class);
    }

    public void deleteScope(String scopeId) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope/" + scopeId;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth scope");
        }
    }


    private String createOauthBaseUrl() {
        String portUrl = "";
        if (this.port != null) {
            portUrl = ":" + this.port;
        }
        return this.schema + "://" + this.host + portUrl + this.oauthBasePath;
    }

    private String createRestBaseUrl() {
        String portUrl = "";
        if (this.port != null) {
            portUrl = ":" + this.port;
        }
        return this.schema + "://" + this.host + portUrl + this.restBasePath;
    }

}
