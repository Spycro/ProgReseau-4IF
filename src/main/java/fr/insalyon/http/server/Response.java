package fr.insalyon.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Réponse constituée par le WebServer
 */
public class Response {
    private int responseCode;
    private String contentType;
    private byte[] body;
    private String userAgent;
    Socket remote;

    /**
     * Constructeur de Reponse
     * @param r la socket
     */
    public Response(Socket r){
        remote = r;
        contentType = "text/html";
    }

    /**
     * Met a jour le code de la réponse
     * @param responseCode le code de la réponse
     */
    public void setResponseCode(int responseCode){
        this.responseCode = responseCode;
    }

    /**
     * Met a jour le corps de la réponse
     * @param body le corps de la réponse
     */
    public void setBody(byte[] body){
        this.body = body;
    }

    /**
     * Met a jour le type de contenu
     * @param contentType le type de contenu
     */
    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    /**
     * Met a jour le userAgent
     * @param ua le userAgent
     */
    public void setUserAgent(String ua){
        userAgent = ua;
    }

    /**
     * Envoi la réponse
     */
    public void send() throws IOException {

        OutputStream os = remote.getOutputStream();
        PrintWriter pw = new PrintWriter(remote.getOutputStream());
        switch(responseCode){
            case 200:
                pw.println("HTTP/1.0 200 OK");
                break;
            case 404:
                pw.println("HTTP/1.0 404 File Not Found");
                break;
            case 405:
                pw.println("HTTP/1.0 405 Method not allowed");
            default:
                pw.println("HTTP/1.0 500 Internal Server Error");
                break;
        }
        pw.println(userAgent);
        if(contentType != null){
            pw.println(contentType);
        }
        else {
            pw.println("Content-Type: text/html");
        }
        pw.println();
        pw.flush();
        if(body != null) {
            os.write(body);
            os.flush();
        }


    }

}
