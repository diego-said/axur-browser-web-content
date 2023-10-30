package com.axreng.backend.util;

import com.axreng.backend.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchUtils {

    public final static int MAX_ID_LENGTH = 8;
    private final static String ID_VALID_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static Logger logger = LoggerFactory.getLogger(SearchUtils.class);

    private final static SecureRandom secureRnd = new SecureRandom();

    public static String randomId() {
        StringBuilder sb = new StringBuilder(MAX_ID_LENGTH);
        for(int i = 0; i < MAX_ID_LENGTH; i++)
            sb.append(ID_VALID_SYMBOLS.charAt(secureRnd.nextInt(ID_VALID_SYMBOLS.length())));
        return sb.toString();
    }

    public static boolean isSearchIdValid(String searchId) {
        return searchId != null &&
                !searchId.isBlank() &&
                searchId.length() == MAX_ID_LENGTH;
    }

    public static boolean isKeywordFound(String keyword, List<String> content) {
        long total = content.stream()
                .filter(s -> s.toLowerCase().indexOf(keyword.toLowerCase()) != -1)
                .count();
        return total > 0;
    }

    public static List<String> getLinks(HttpResponse response) {
        if(response.getStatus() >= 200 && response.getStatus() <= 299) {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(response.getContentAsByteArray())
                    ));

            HTMLEditorKit.Parser parser = new ParserDelegator();
            final List<String> links = new ArrayList<>();
            try {
                parser.parse(bufferedReader, new HTMLEditorKit.ParserCallback() {
                    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
                        if (t == HTML.Tag.A) {
                            Object link = a.getAttribute(HTML.Attribute.HREF);
                            if (link != null) {
                                try {
                                    URI baseURL = new URI(response.getUrl());
                                    URI uri = new URI(String.valueOf(link));
                                    links.add(baseURL.resolve(uri).toString());
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }, true);
            } catch (IOException e) {
                logger.error("Error parsing response for url: " + response.getUrl(), e);
                throw new RuntimeException(e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error("Error in method [getLinks]: ", e);
                }
            }
            return links;
        }
        return Collections.emptyList();
    }

}
