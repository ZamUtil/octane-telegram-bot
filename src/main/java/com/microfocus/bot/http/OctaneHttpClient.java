package com.microfocus.bot.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfocus.bot.Constants;
import com.microfocus.bot.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OctaneHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(OctaneHttpClient.class);

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BASE_API_PATH = "/api/shared_spaces/1001/workspaces/1002";
    private static final String AUTH_URL = "/authentication/sign_in";
    private static final String MY_WORK_URL = BASE_URL + BASE_API_PATH + "/user_items";
    private static final String WORK_ITEM_URL = BASE_URL+ BASE_API_PATH + "/work_items";
    private static final String COMMENTS_URL = BASE_URL + BASE_API_PATH + "/comments";
    private static final String COMMENTS_URL_INT = BASE_URL + "/internal-api/shared_spaces/1001/workspaces/1002" + "/comments";
    private static final String USER_ID_URL = "/admin/users";

    private static final String USER_ITEM_URL = BASE_URL + BASE_API_PATH + "/workspace_users";

    public static final OctaneHttpClient INSTANCE = new OctaneHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private final HttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
            .useSystemProperties()
            .build();

    private OctaneHttpClient() {
    }

    public String login(OctaneAuth octaneAuth) {
        try {
            String data = objectMapper.writeValueAsString(octaneAuth);
            processPost(BASE_URL + AUTH_URL, data);
            String result = processGet(BASE_URL + USER_ID_URL + prepareGetUserIdQuery(octaneAuth.getUser()));
            OctaneUser octaneUser = objectMapper.readValue(result, OctaneUser.class);
            return octaneUser.getId();
        } catch (Exception ignored) {
            ignored.getStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public List<MyWorkFollowItem> getMyWork(OctaneAuth octaneAuth, Long userId) {
        try{
            login(octaneAuth);
            String response = processGet(MY_WORK_URL + prepareGetMyWorkQuery(userId));
            MyWorkItemsContainer myWorkItemsContainer = objectMapper.readValue(response, MyWorkItemsContainer.class);
            List<MyWorkFollowItem> myWorkFollowItems = myWorkItemsContainer.getData()
                    .stream()
                    .filter(followItem -> followItem.getWorkItem() != null )
                    .collect(Collectors.toList());
            myWorkFollowItems
                    .forEach(followItem -> {
                        followItem.setWorkItem(getWorkItemById(followItem.getWorkItem().getId()));
                        followItem.setAuthor(getUserById(userId.toString()));
                    });
            return myWorkFollowItems;
        } catch (Exception e) {
            logger.error("Error while reading new comments", e);
            return Collections.emptyList();
        }
    }

    public List<Comment> getNewComments(OctaneAuth octaneAuth, Long userId) {
        try {
            login(octaneAuth);
            String response = processGet(COMMENTS_URL + prepareGetCommentQuery(userId));
            CommentsContainer commentsResponse = objectMapper.readValue(response, CommentsContainer.class);

            List<Comment> comments = commentsResponse.getData();

            comments.forEach(comment -> {
                comment.setWorkItem(getWorkItemById(comment.getOwnerWorkItem().getId()));
                comment.setAuthor(getUserById(comment.getAuthor().getId()));
                markCommentAsRead(comment.getId());
            });

            return comments;
        } catch (Exception e) {
            logger.error("Error while reading new comments", e);
        }
        return new ArrayList<>();
    }

    public List<MyWorkFollowItem> getNewMyWork(OctaneAuth octaneAuth, Long userId) {
        try {
            login(octaneAuth);
            String response = processGet(MY_WORK_URL + prepareGetNewMyWorkQuery(userId));
            MyWorkItemsContainer myWorkItemsContainer = objectMapper.readValue(response, MyWorkItemsContainer.class);
            List<MyWorkFollowItem> myWorkFollowItems = myWorkItemsContainer.getData();
            myWorkFollowItems.stream()
                    .filter(followItem -> followItem.getWorkItem() != null)
                    .forEach(followItem -> {
                        followItem.setWorkItem(getWorkItemById(followItem.getWorkItem().getId()));
                        followItem.setAuthor(getUserById(userId.toString()));

                        markMyWorkAsRead(followItem.getId());
                    });
            return myWorkFollowItems;
        } catch (Exception e) {
            logger.error("Error while reading new comments", e);
            return Collections.emptyList();
        }
    }

    private void markCommentAsRead(Long commentId) {
        try {
            processPut(COMMENTS_URL_INT + "/" + commentId + "/mark_as_read", "{\"id\":\"" + commentId + "\"}");
        } catch (Exception ignored) {
        }
    }

    private void markMyWorkAsRead(Long myWorkId) {
        try {
            processPut(MY_WORK_URL + "/" + myWorkId, "{\"is_new\":false,\"id\":\"" + myWorkId + "\"}");
        } catch (Exception ignored) {
        }
    }

    public void postComment(OctaneAuth octaneAuth, Pair<Long, String> itemData, String text) {
        try {
            login(octaneAuth);

            Comment comment = new Comment();
            comment.setText(text);
            comment.setOwnerWorkItem(new Comment.OwnerItem(itemData.getLeft(), itemData.getRight()));

            CommentsContainer commentsReq = new CommentsContainer();
            commentsReq.setData(Collections.singletonList(comment));

            String data = objectMapper.writeValueAsString(commentsReq);
            processPost(COMMENTS_URL, data);
        } catch (Exception ignored) {
        }
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
                //What is it and goal?
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

    private String processPut(String uri, String putData) {
        logger.debug("Start executing POST request to url=\"{}\" with payload={}", uri, putData);

        HttpPut putRequest = new HttpPut(uri);
        putRequest.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        putRequest.setEntity(new StringEntity(putData, "UTF-8"));

        HttpResponse response = null;
        String result = "";

        try {
            response = httpClient.execute(putRequest);
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

    private String prepareGetCommentQuery(Long userId) {
        return "?fields=order_number,author,text,owner_run,owner_test,owner_work_item,owner_requirement,owner_bdd_spec,owner_process,owner_planning_info,owner_task,owner_issue,id" +
                "&limit=10&offset=0&order_by=-creation_time" +
                "&query=%22((mention_user%3D%7B(id%3D" + userId + ")%7D);(my_new_items_owner%3D%7Bid%3D" + userId + "%7D))%22);";
    }

    private String prepareGetMyWorkQuery(Long userId) {
        return "?fields=my_follow_items_work_item&limit=10&offset=0&order_by=-creation_time" +
                "&query=%22((user%3D%7Bid%3D" + userId + "%7D))%22);";
    }

    private String prepareGetNewMyWorkQuery(Long userId) {
        return "?fields=my_follow_items_work_item&limit=10&offset=0&order_by=-creation_time" +
                "&query=%22((user%3D%7Bid%3D" + userId + "%7D;is_new=true))%22);";
    }
    private String prepareGetUserIdQuery(String name) {
        return "?fields=id&query=%22(name%3D'" + name +"')%22";
    }



    public WorkItem getWorkItemById(Long id) {
        try {
            String response = processGet(WORK_ITEM_URL + "/" + id + "?fields=name,description");
            return objectMapper.readValue(response, WorkItem.class);
        } catch (Exception ignored) {
        }
        return null;
    }

    public OctaneUser getUserById(String id) {
        try {
            String response = processGet(USER_ITEM_URL + "?fields=first_name,last_name,name&limit=111&offset=0&order_by=id&query=%22(id%3D" + id + ")%22");
            return objectMapper.readValue(response, OctaneUser.class);
        } catch (Exception ignored) {
        }
        return null;
    }
}
