package com.axreng.backend.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HttpResponse {

    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final String url;

    private final int status;

    private final List<String> content;

    public HttpResponse(String url, int status, List<String> content) {
        this.url = url;
        this.status = status;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getContent() {
        return content;
    }

    public byte[] getContentAsByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outputStream);

        try {
            for (String line : content) {
                out.writeUTF(line);
            }
            out.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.error("Error in method [getContentAsByteArray]: ", e);
            }
        }
    }

    public boolean isSuccessful() {
        return status >= 200 && status <= 299;
    }

    public boolean isServerError() {
        return status >= 500 && status <= 599;
    }

}
