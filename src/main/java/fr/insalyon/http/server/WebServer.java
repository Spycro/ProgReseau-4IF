///A Simple Web Server (WebServer.java)

package fr.insalyon.http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {


    private final String pwd = "C:\\Users\\Kaolyfin\\IdeaProjects\\ProgReseau-4IF\\resources";
    private String contentType;


    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                byte[] data = new byte[0];
                String str = ".";
                String request = "";
                boolean firstline = true;

                List<String> header = new ArrayList<>();
                String body = "";

                //Read header
                while (str != null && !str.equals("")){
                    str = "";
                    char c;
                    while ((c=(char)in.read()) != '\n'){
                        if(c != '\r'){
                            str += c;
                        }
                    }
                    header.add(str);
                    System.out.println(str);
                }

                //Read body
                int contentLength = 0;
                for(String headerTag : header){
                    if(headerTag.contains("Content-Length")){
                        String[] splitted = headerTag.split(" ");
                        contentLength = Integer.parseInt(splitted[1]);
                        break;
                    }
                }

                if(contentLength != 0)
                    body = remote.getInputStream().readNBytes(contentLength).toString();
                System.out.println("body :" + body);

                if(header.get(0).contains("GET")) {
                    data = doGET(header.get(0));
                }
                else if(header.get(0).contains("POST")){
                    data = doPOST(header.get(0), body);
                }
                else if(header.get(0).contains("PUT")){
                    data = doPUT(header, in);
                }
                else if(header.get(0).contains("DELETE")){

                }
                // Send the response
                // Send the headers
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: "+contentType);
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.flush();
                remote.getOutputStream().write(data, 0, data.length);

                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }


    public byte[] doGET(String location){
        location = location.substring(4);
        int indexOfSpace = location.indexOf(" ");
        location = location.substring(0, indexOfSpace);
        byte[] data = new byte[0];
        try {
            File file = new File(pwd + location);
            contentType = Files.probeContentType(file.toPath());
            data = Files.readAllBytes(file.toPath());

        } catch(FileNotFoundException e){

            return "File Not Found".getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public byte[] doPOST(String location, String body) throws IOException {

        String str = ".";
        String variable = "";
        String value = "";
        StringBuilder sb = new StringBuilder();
        sb.append("<H1>");
        location = location.substring(5);
        location = location.substring(0, location.indexOf(' '));
            if(body.matches("(?:\\w+=\\w+&?)+")){
                variable = str.substring(0, str.indexOf('='));
                str = str.substring(str.indexOf('=') + 1);
                value = str.substring(0, str.indexOf('&'));
                str = str.substring(str.indexOf('&') + 1);
                sb.append("Variable" + variable + " egale a " + value +"\n");
            }


        sb.append("<H1>");
        byte[] data = sb.toString().getBytes();

        return data;
    }

    public byte[] doPUT(List<String> header, BufferedReader in) throws IOException{
        String location = header.get(0).substring(4);
        int indexOfSpace = location.indexOf(" ");
        location = location.substring(0, indexOfSpace);
        int contentLength = 0;
        //read request :
        for(String headerTag : header){
            if(headerTag.contains("Content-Length")){
                String[] splitted = headerTag.split(" ");
                contentLength = Integer.parseInt(splitted[1]);
            }
        }
        byte[] data = new byte[contentLength];

        File file = new File(pwd);

        if(file.exists()) {
            return "Resource already exist".getBytes();
        }
        file.createNewFile();


        return data;

    }

}
