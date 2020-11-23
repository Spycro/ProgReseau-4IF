package fr.insalyon.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Response {
    private int responseCode;
    private String contentType;
    private byte[] body;
    private String userAgent;
    Socket remote;

    public Response(Socket r){
        remote = r;
    }

    public void setResponseCode(int responseCode){
        this.responseCode = responseCode;
    }

    public void setBody(byte[] body){
        this.body = body;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    public void setUserAgent(String ua){
        userAgent = ua;
    }

    public void send() throws IOException {

        OutputStream os = remote.getOutputStream();
        PrintWriter pw = new PrintWriter(remote.getOutputStream());
        switch(responseCode){
            case 200:
                pw.println("HTTP/1.0 200 OK");
            case 404:
                pw.println("HTTP/1.0 404 File Not Found");
        }
        pw.println(userAgent);
        pw.println(contentType);
        pw.println();
        pw.flush();
        if(body != null) {
            os.write(body);
            os.flush();
        }


    }

}
