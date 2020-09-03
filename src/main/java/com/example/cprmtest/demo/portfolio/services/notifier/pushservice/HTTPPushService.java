package com.example.cprmtest.demo.portfolio.services.notifier.pushservice;

import com.example.cprmtest.demo.exceptions.user.PushNotificationException;
import com.example.cprmtest.demo.model.dto.CustomerNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
//simple wrapper around java.net packages
public class HTTPPushService<T> extends PushService<T>{

    Logger logger = LoggerFactory.getLogger(HTTPPushService.class);

    HttpURLConnection connection;


    @Override
    public void initializeConnection(String location) {
        this.location = location;
        connection = getConnection();
    }

    @Override
    public boolean sendAndCheck(CustomerNotification<T> objectToSend) {
        if(connection == null) {
            return false;
        }

        String requestBody = null;
        try {
            requestBody = serializeToJson(objectToSend);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new PushNotificationException("Couldn't serialize object to send");
        }
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new PushNotificationException("Couldn't write to connection" + location);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString().equals("true");
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new PushNotificationException("Couldn't read response from" + location);
        }
    }

    private String serializeToJson(CustomerNotification<T> objectToSend) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(objectToSend);
    }

    private HttpURLConnection getConnection() {
        URL url = null;
        try {
            url = new URL(location);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            throw new PushNotificationException("Given URL is not valid! " + location);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new PushNotificationException("Couldn't open connection to URL: " + location);
        }
        try {
            assert con != null;
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
        } catch (ProtocolException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new PushNotificationException("Couldn't estabilish connection properties to " + location);
        }
        con.setDoOutput(true);
        return con;
    }
}
