package org.uengine.iam.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.uengine.iam.client.model.OauthAvatar;
import org.uengine.iam.client.model.OauthClient;
import org.uengine.iam.client.model.OauthUser;
import org.uengine.iam.client.model.OauthScope;
import org.uengine.iam.client.util.HttpUtils;
import org.uengine.iam.client.util.JsonUtils;
import org.uengine.iam.client.util.StringUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2016. 9. 8..
 */
public class IamClient {

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

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        return JsonUtils.convertValue(unmarshal, OauthUser.class);
    }

    public List<OauthUser> getAllUser(int page, int size) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user" + queryString;

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
        for (Map map : list) {
            result.add(JsonUtils.convertValue(map, OauthUser.class));
        }
        return result;
    }

    public OauthUser getUser(String userName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user/search/findByUserName" + queryString;

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
        return JsonUtils.convertValue(unmarshal, OauthUser.class);
    }

    public OauthUser updateUser(OauthUser oauthUser) throws Exception {
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

        HttpResponse response = httpUtils.makeRequest("PUT", url, JsonUtils.marshal(params), headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new Exception("Failed to update oauth user");
        }
        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        return JsonUtils.convertValue(unmarshal, OauthUser.class);
    }

    public void deleteUser(String userName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/user" + queryString;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth user");
        }
    }

    public OauthAvatar createUserAvatar(File file, String contentType, String userName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("contentType", contentType);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar" + queryString;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeFileRequest("POST", url, file, headers);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new Exception("Failed to create oauth user avatar");
        }

        HttpEntity entity = response.getEntity();
        String responseText = EntityUtils.toString(entity);

        Map unmarshal = JsonUtils.unmarshal(responseText);
        return JsonUtils.convertValue(unmarshal, OauthAvatar.class);
    }

    public void deleteUserAvatar(String userName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar" + queryString;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("DELETE", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 204) {
            throw new Exception("Failed to delete oauth user avatar");
        }
    }

    public void getUserAvatar(String userName, OutputStream outputStream) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        String queryString = HttpUtils.createGETQueryString(params);

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/avatar" + queryString;

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("client-key", this.clientId);
        headers.put("client-secret", this.clientSecret);

        HttpResponse response = httpUtils.makeRequest("GET", url, null, headers);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to get oauth user avatar");
        }
        response.getEntity().writeTo(outputStream);
    }

    public List<OauthClient> listAllClients() throws Exception {

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
        for (Map map : list) {
            result.add(JsonUtils.convertValue(map, OauthClient.class));
        }
        return result;
    }

    public OauthClient getClient(String clientKey) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/client/" + clientKey;

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


    public List<OauthScope> listAllScopes() throws Exception {

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
        for (Map map : list) {
            result.add(JsonUtils.convertValue(map, OauthScope.class));
        }
        return result;
    }

    public OauthScope getScope(String name) throws Exception {

        HttpUtils httpUtils = new HttpUtils();
        String url = createRestBaseUrl() + "/scope/" + name;

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
        return JsonUtils.convertValue(unmarshal, OauthScope.class);
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
