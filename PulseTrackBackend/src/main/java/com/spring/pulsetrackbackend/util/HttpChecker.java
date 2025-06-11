package com.spring.pulsetrackbackend.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpChecker {

    public static Result checkUrl(String url) {
        long start = System.currentTimeMillis();
        int status = 0;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(3000); // 3 seconds timeout
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            connection.connect();
            status = connection.getResponseCode();
        } catch (Exception e) {
            status = 0; // Treat as unreachable
        }

        long timeTaken = System.currentTimeMillis() - start;
        return new Result(status, timeTaken);
    }

    public static class Result {
        public final int statusCode;
        public final long responseTime;

        public Result(int statusCode, long responseTime) {
            this.statusCode = statusCode;
            this.responseTime = responseTime;
        }
    }
}