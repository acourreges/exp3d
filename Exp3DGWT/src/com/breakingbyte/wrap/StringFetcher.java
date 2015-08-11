package com.breakingbyte.wrap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;


public class StringFetcher {
    
    public boolean fetched = false;
    
    public String filePath = null;
    
    public String content;
    
    public StringFetcher(String filePath) {
        this.filePath = filePath;
    }

    public void fetch() {
        
 RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "assets/" + filePath);
        
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived (Request request, Response response) {
                    String contentText = response.getText();
                    content = contentText;
                    fetched = true;
                }
    
                @Override
                public void onError (Request request, Throwable exception) {
                    GWT.log("Text file download error! " + filePath, exception);
                }
               });
            } catch (RequestException e) {
                GWT.log("Text file request exception! " + filePath, e);
            }

    }
    
}
