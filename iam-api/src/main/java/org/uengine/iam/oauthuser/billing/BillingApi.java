package org.uengine.iam.oauthuser.billing;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uengine.iam.util.HttpUtils;
import org.uengine.iam.util.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingApi {

    private Logger logger = LoggerFactory.getLogger(BillingApi.class);

    @Autowired
    private BillingConfig billingConfig;

    public Map getAccountByExternalKey(String userName) {
        String method = "GET";
        String path = "/rest/v1/accounts?externalKey=" + userName;

        Map headers = new HashMap();
        try {
            HttpResponse httpResponse = this.apiRequest(method, path, null, headers);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                return JsonUtils.marshal(result);
            } else {
                logger.error("Not found account");
                return null;
            }
        } catch (IOException ex) {
            logger.error("Failed to request account");
            return null;
        }
    }

    public Map<String, Object> generateSyncBody(String userName, Map<String, Object> metaData) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("externalKey", userName);
        Map<String, String> generateBody = billingConfig.getAccountSyncBody();
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(JsonUtils.marshal(metaData));
        for (String key : generateBody.keySet()) {
            String value = generateBody.get(key);
            if (value.startsWith("$")) {
                try {
                    value = JsonPath.read(json, value);
                } catch (Exception ex) {
                    value = null;
                }
            }
            if (value != null) {
                if (key.equals("billCycleDayLocal")) {
                    body.put(key, Integer.parseInt(value));
                } else {
                    body.put(key, value);
                }
            }
        }

        //validate
        String[] requiredFields = new String[]{"name", "email", "billCycleDayLocal", "currency", "locale"};
        for (String field : requiredFields) {
            if (!body.containsKey(field) || body.get(field) == null) {
                throw new Exception(field + " is required to create billing account ");
            }
        }
        return body;
    }

    public void createAccount(String userName, Map<String, Object> metaData) {
        String method = "POST";
        String path = "/rest/v1/accounts";

        try {
            Map headers = new HashMap();
            Map<String, Object> body = this.generateSyncBody(userName, metaData);
            String marshal = JsonUtils.marshal(body);

            HttpResponse httpResponse = this.apiRequest(method, path, marshal, headers);
            if (httpResponse.getStatusLine().getStatusCode() != 201 && httpResponse.getStatusLine().getStatusCode() != 200) {
                logger.error("Failed to create account {}", body);
                throw new RuntimeException("Failed to create account");
            }
            String s = EntityUtils.toString(httpResponse.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create account", ex);
        }
    }

    public void updateAccount(String accountId, String userName, Map<String, Object> metaData) {

        String method = "PUT";
        String path = "/rest/v1/accounts/" + accountId;

        try {
            Map headers = new HashMap();
            Map<String, Object> body = this.generateSyncBody(userName, metaData);
            body.remove("billCycleDayLocal");
            body.remove("currency");
            String marshal = JsonUtils.marshal(body);

            HttpResponse httpResponse = this.apiRequest(method, path, marshal, headers);
            if (httpResponse.getStatusLine().getStatusCode() != 201 && httpResponse.getStatusLine().getStatusCode() != 200) {
                logger.error("Failed to update account {}", body);
                throw new RuntimeException("Failed to update account");
            }
            String s = EntityUtils.toString(httpResponse.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to update account", ex);
        }
    }

    public List<Map> getAccountBundles(String accountId) {
        String method = "GET";
        String path = "/rest/v1/accounts/" + accountId + "/bundles?audit=NONE";

        Map headers = new HashMap();
        try {
            HttpResponse httpResponse = this.apiRequest(method, path, null, headers);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                return JsonUtils.unmarshalToList(result);
            } else {
                logger.error("Not found account bundles");
                return null;
            }
        } catch (IOException ex) {
            logger.error("Failed to request account bundles");
            return null;
        }
    }

    public HttpResponse apiRequest(String method, String path, String data, Map headers) throws IOException {
        Map requiredHeaders = new HashMap();
        requiredHeaders.put("Authorization", "Basic " + billingConfig.getAuthentication());
        requiredHeaders.put("Content-Type", "application/json");
        requiredHeaders.put("Accept", "application/json");
        requiredHeaders.put("X-organization-id", billingConfig.getOrganization());
        requiredHeaders.putAll(headers);

        String url = billingConfig.getUrl() + path;
        HttpUtils httpUtils = new HttpUtils();
        HttpResponse httpResponse = httpUtils.makeRequest(method, url, data, requiredHeaders);
        return httpResponse;
    }
}
