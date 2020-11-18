///A Simple Web Server (WebServer.java)

package fr.insalyon.http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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


    private final String pwd = "/home/lucas/Documents/IF/ProgReseau/resources";
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
                while (str != null && !str.equals("")) {
                    str = in.readLine();
                    if(firstline){
                        if(str.contains("GET")) {
                            data = doGET(str);
                        }
                        else if(str.contains("POST")){

                        }
                        else if(str.contains("PUT")){

                        }
                        else if(str.contains("DELETE")){

                        }
                    }
                    firstline = false;

                }
                // Send the response
                // Send the headers
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.flush();
                remote.getOutputStream().write(data, 0, data.length);
                System.out.write(data);

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

            data = Files.readAllBytes(file.toPath());

        } catch(FileNotFoundException e){

            return "File Not Found".getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }



}
