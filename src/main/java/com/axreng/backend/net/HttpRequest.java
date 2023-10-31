package com.axreng.backend.net;

import com.axreng.backend.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.axreng.backend.config.ConfigNames.CONNECTION_TIMEOUT;
import static com.axreng.backend.config.ConfigNames.READ_TIMEOUT;

public class HttpRequest {

    private final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final String url;

    private final int connectionTimeout;
    private final int readTimeout;

    public HttpRequest(String url) {
        this.url = url;
        ConfigLoader configLoader = ConfigLoader.getInstance();
        var configValue = configLoader.getConfigAsInteger(CONNECTION_TIMEOUT);
        this.connectionTimeout = configValue.orElse(0);

        configValue = configLoader.getConfigAsInteger(READ_TIMEOUT);
        this.readTimeout = configValue.orElse(0);
    }

    public HttpResponse get()  {
        HttpURLConnection httpURLConnection = null;

        try {
            URL requestUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(connectionTimeout);
            httpURLConnection.setReadTimeout(readTimeout);

            BufferedReader in = new BufferedReader(getResponseReader(httpURLConnection));
            String inputLine;
            final List<String> content = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                content.add(inputLine);
            }
            in.close();
            return new HttpResponse(url, httpURLConnection.getResponseCode(), content);
        } catch (IOException e) {
            logger.error("HttpRequest method [GET] failed for url: " + url, e);
            throw new RuntimeException(e);
        } finally {
            if(httpURLConnection != null)
                httpURLConnection.disconnect();
        }
    }

    private Reader getResponseReader(HttpURLConnection httpURLConnection) {
        try {
            final int status = httpURLConnection.getResponseCode();
            if (status > 299) {
                return new InputStreamReader(httpURLConnection.getErrorStream());
            } else {
                return new InputStreamReader(httpURLConnection.getInputStream());
            }
        } catch (IOException e) {
            logger.error("HttpRequest method [GET] failed for url: " + url, e);
            throw new RuntimeException(e);
        }
    }
}
