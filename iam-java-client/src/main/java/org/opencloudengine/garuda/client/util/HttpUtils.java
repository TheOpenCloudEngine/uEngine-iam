package org.opencloudengine.garuda.client.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2016. 2. 22..
 */
public class HttpUtils {

    protected static String DEFAULT_ENCODING_FOR_URL = "UTF-8";

    public HttpResponse makeFileRequest(String type, String uri, File file, Map<String, String> headers) throws IOException {
        HttpRequestBase request = null;

        switch (type) {
            case "GET":
                request = new HttpGet(uri);
                break;
            case "POST":
                request = new HttpPost(uri);
                break;
            case "PUT":
                request = new HttpPut(uri);
                break;
            case "DELETE":
                request = new HttpDelete(uri);
                break;
            default:
                throw new RuntimeException("Invalid HTTP request type: " + type);
        }

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        if (file != null) {
            try {
                if (request instanceof HttpPut)
                    ((HttpPut) request).setEntity(new FileEntity(file, "binary/octet-stream"));
                if (request instanceof HttpPost) {
                    ((HttpPost) request).setEntity(new FileEntity(file, "binary/octet-stream"));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        HttpClient client = new DefaultHttpClient();
        System.out.println("Making " + request.getMethod() + " request to: " + uri);
        HttpResponse httpResponse = client.execute(request);

        return httpResponse;
    }

    public HttpResponse makeRequest(String type, String uri, String data, Map<String, String> headers) throws IOException {
        HttpRequestBase request = null;

        switch (type) {
            case "GET":
                request = new HttpGet(uri);
                break;
            case "POST":
                request = new HttpPost(uri);
                break;
            case "PUT":
                request = new HttpPut(uri);
                break;
            case "DELETE":
                request = new HttpDelete(uri);
                break;
            default:
                throw new RuntimeException("Invalid HTTP request type: " + type);
        }

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        if (data != null) {
            try {
                if (request instanceof HttpPut)
                    ((HttpPut) request).setEntity(new StringEntity(data, "UTF-8"));
                if (request instanceof HttpPost)
                    ((HttpPost) request).setEntity(new StringEntity(data, "UTF-8"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        HttpClient client = new DefaultHttpClient();
        System.out.println("Making " + request.getMethod() + " request to: " + uri);
        HttpResponse httpResponse = client.execute(request);

        return httpResponse;
    }

    public static String createGETQueryString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder("");

        List<String> listOfParams = new ArrayList<String>();
        for (String param : params.keySet()) {
            listOfParams.add(param + "=" + encodeString(params.get(param) + ""));
        }

        if (!listOfParams.isEmpty()) {
            String query = org.apache.commons.lang.StringUtils.join(listOfParams, "&");
            sb.append("?");
            sb.append(query);
        }

        return sb.toString();
    }

    public static String createPOSTQueryString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder("");

        List<String> listOfParams = new ArrayList<String>();
        for (String param : params.keySet()) {
            listOfParams.add(param + "=" + encodeString(params.get(param) + ""));
        }

        if (!listOfParams.isEmpty()) {
            String query = org.apache.commons.lang.StringUtils.join(listOfParams, "&");
            sb.append(query);
        }

        return sb.toString();
    }

    public static String encodeString(String name) throws NullPointerException {
        String tmp = null;

        if (name == null)
            return null;

        try {
            tmp = java.net.URLEncoder.encode(name, DEFAULT_ENCODING_FOR_URL);
        } catch (Exception e) {
        }

        if (tmp == null)
            throw new NullPointerException();

        return tmp;
    }

    public static String decodeString(String name) throws NullPointerException {
        String tmp = null;

        if (name == null)
            return null;

        try {
            tmp = URLDecoder.decode(name, DEFAULT_ENCODING_FOR_URL);
        } catch (Exception e) {
        }

        if (tmp == null)
            throw new NullPointerException();

        return tmp;
    }
}
