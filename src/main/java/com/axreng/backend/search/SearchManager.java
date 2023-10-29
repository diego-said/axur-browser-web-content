package com.axreng.backend.search;

import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;
import com.axreng.backend.search.entities.Search;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchManager {

    private final String baseUrl = System.getenv("BASE_URL");

    private static SearchManager INSTANCE;

    private final List<Search> searchList;

    public SearchManager() {
        this.searchList = new ArrayList<>();
    }

    public synchronized static SearchManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchManager();
        }
        return INSTANCE;
    }

    public synchronized void addSearch(Search search) {
        searchList.add(search);
    }

    public synchronized void performNextSearchInQueue() {
        final Search search = searchList.remove(0);
        final HttpRequest request = new HttpRequest(baseUrl);
        final HttpResponse response = request.get();

        getLinks(response).stream().forEach(link -> {
            System.out.println(link);
            try {
                URI baseURL = new URI(baseUrl);
                URI uri = new URI(String.valueOf(link));
                System.out.println(baseURL.getHost().equals(uri.getHost()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private List<String> getLinks(HttpResponse response) {
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
                                    URI baseURL = new URI(baseUrl);
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
                throw new RuntimeException(e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return links;
        }
        return Collections.emptyList();
    }

}