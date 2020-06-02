package com.microfocus.bot.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OctaneHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(OctaneHttpClient.class);

    private static final String BASE_URL = "http://localhost:8080";
    private static final String AUTH_URL = "/authentication/sign_in";
    private static final String MY_WORK_URL = "/api/shared_spaces/1001/workspaces/1002/user_items";

    public static final OctaneHttpClient INSTANCE = new OctaneHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
    private final HttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
            .useSystemProperties()
            .build();

    private OctaneHttpClient() {
    }

    public boolean login(OctaneAuth octaneAuth) {
        try {
            String data = objectMapper.writeValueAsString(octaneAuth);
            processPost(BASE_URL + AUTH_URL, data);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public String getMyWork(OctaneAuth octaneAuth) {
        login(octaneAuth);
        String response = processGet(BASE_URL + MY_WORK_URL);//TODO provide correct query

        //TODO parse and return
        return response;
    }

    private String processGet(String uri) {
        logger.trace("Start executing GET request to url=\"{}\"", uri);

        HttpGet getRequest = new HttpGet(uri);
        getRequest.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        HttpResponse response = null;
        String result = "";

        try {
            response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw createHttpError(response);
            }

            result = parseResponse(response);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            ExceptionUtils.rethrow(e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        logger.debug("End executing GET request to url=\"{}\" and receive this result={}", uri, result);

        return result;
    }

    private String processPost(String uri, String putData) {
        logger.debug("Start executing POST request to url=\"{}\" with payload={}", uri, putData);

        HttpPost postRequest = new HttpPost(uri);
        postRequest.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        postRequest.setEntity(new StringEntity(putData, "UTF-8"));

        HttpResponse response = null;
        String result = "";

        try {
            response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                throw createHttpError(response);
            }

            result = parseResponse(response);
            logger.debug("End executing POST request to url=\"{}\"", uri);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            ExceptionUtils.rethrow(e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        return result;
    }

    private String parseResponse(HttpResponse response) throws IOException {
        String result = "";
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        StringBuilder sb = new StringBuilder(1024);
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        result = sb.toString();
        return result;
    }

    private Exception createHttpError(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        return new Exception(EnglishReasonPhraseCatalog.INSTANCE.getReason(status.getStatusCode(), null) + ": " + status.getReasonPhrase());
    }

}
