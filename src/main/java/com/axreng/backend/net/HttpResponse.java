package com.axreng.backend.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HttpResponse {

    private final int status;

    private final List<String> content;

    public HttpResponse(int status, List<String> content) {
        this.status = status;
        this.content = content;
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
                throw new RuntimeException(e);
            }
        }
    }

}
