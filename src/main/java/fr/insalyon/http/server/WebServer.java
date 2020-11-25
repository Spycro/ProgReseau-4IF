///A Simple Web Server (WebServer.java)

package fr.insalyon.http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * WebServer qui permet de répondre aux requetes
 */
public class WebServer {


    private String pwd;
    private String contentType;


    /**
     * Démarrage de l'application.
     *
     * @param args Paramètres de la ligne de commande
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: java ChatServer <WebServer port> <WebServer resourcePath>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        WebServer ws = new WebServer();
        ws.start(port, args[1]);
    }

    /**
     * Démarrage du WebServer
     * @param port le port du serveur
     * @param resourcePath le chemin d'accès aux ressources
     */
    protected void start(int port, String resourcePath) {
        ServerSocket s;
        pwd = resourcePath;

        System.out.println("Webserver starting up on port " + port);
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(port);
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

                // read the data sent.
                byte[] data = new byte[0];
                String str = ".";

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


                int contentLength = 0;
                for(String headerTag : header){
                    if(headerTag.contains("Content-Length")){
                        String[] splitted = headerTag.split(" ");
                        contentLength = Integer.parseInt(splitted[1]);
                        break;
                    }
                }
                //Read body with content length
                char c;
                for(int i = 0 ; i < contentLength ; ++i){
                    c = (char)in.read();
                    body += c;
                }
                System.out.println("body : " + body);

                Response response = new Response(remote);

                if(header.get(0).contains("GET")) {
                    String resourceLocation = header.get(0).substring(4, header.get(0).lastIndexOf(' '));
                    doGET(resourceLocation, response);
                }
                else if(header.get(0).contains("HEAD")) {
                    String resourceLocation = header.get(0).substring(5, header.get(0).lastIndexOf(' '));
                    doHEAD(resourceLocation, response);
                }
                else if(header.get(0).contains("POST")){
                    String resourceLocation = header.get(0).substring(5, header.get(0).lastIndexOf(' '));
                    doPOST(resourceLocation, body, response);
                }
                else if(header.get(0).contains("PUT")){
                    doPUT(header, body, response);
                }
                else if(header.get(0).contains("DELETE")){
                    String resourceLocation = header.get(0).substring(7, header.get(0).lastIndexOf(' '));
                    doDelete(resourceLocation, response);
                }

                response.setContentType(contentType);
                response.setUserAgent("Server: Bot");

                System.out.println("sending response");
                response.send();
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Réponse à la requete get
     * @param location le chemin d'accès de la ressource
     * @param response réponse à renvoyer
     */
    public void doGET(String location, Response response){
        byte[] data;
        try {
            if (location.equals("/")) location = "/index.html";
            File file = new File(pwd + location);
            contentType = Files.probeContentType(file.toPath());
            data = Files.readAllBytes(file.toPath());
            response.setResponseCode(200);
            response.setBody(data);
        } catch (IOException e) {
            response.setResponseCode(404);
        }

    }

    /**
     * Réponse à la requete head
     * @param location le chemin d'accès de la ressource
     * @param response réponse à renvoyer
     */
    public void doHEAD(String location, Response response){
        try {
            if (location.equals("/")) location = "/index.html";
            File file = new File(pwd + location);
            contentType = Files.probeContentType(file.toPath());
            response.setResponseCode(200);
        } catch (IOException e) {
            response.setResponseCode(404);
        }

    }

    /**
     * Réponse à la requete post
     * @param location le chemin d'accès de la ressource
     * @param body le contenu du crops de la requete
     * @param response réponse à renvoyer
     */
    public void doPOST(String location, String body, Response response){

        String variable = "";
        String value = "";
        String infos = "";
        if (body.matches("(?:\\w+=\\w*&?)+")) {
            while(body.length() != 0){
                variable = body.substring(0, body.indexOf('='));
                body = body.substring(body.indexOf('=') + 1);
                if(body.indexOf('&') != -1){
                    value = body.substring(0, body.indexOf('&'));
                    body = body.substring(body.indexOf('&') + 1);
                }else{
                    value = body;
                    body = "";
                }
                infos += variable + " = " + value + " & ";
                System.out.println("Recuperation de la variable " + variable + " egale a " + value);
            }
        }

        byte[] data;
        try {
            File file = new File(pwd + location);
            contentType = Files.probeContentType(file.toPath());
            data = Files.readAllBytes(file.toPath());
            response.setResponseCode(200);

            if(infos.length() > 1){
                String dataString = new String(data) + "<h1>" +  infos.substring(0,infos.length()-2) + "</h1>";
                data = dataString.getBytes();
            }
            response.setBody(data);

        } catch (IOException e) {
            response.setResponseCode(404);
            response.setBody("Error 404 : File Not Found".getBytes());
        }
    }

    /**
     * Réponse à la requete get
     * @param header le contenu du head de la requete
     * @param body le contenu du crops de la requete
     * @param response réponse à renvoyer
     */
    public void doPUT(List<String> header, String body, Response response) throws IOException{
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

        File file = new File(pwd + location);

        if(file.exists()) {
            response.setResponseCode(200);
            response.setBody("Resource already exist".getBytes());
            return;
        }
        file.createNewFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(body.getBytes());
        bos.flush();
        bos.close();

        response.setResponseCode(200);
        response.setBody("Resources created".getBytes());
        return;
    }

    /**
     * Réponse à la requete get
     * @param location le chemin d'accès de la ressource
     * @param response réponse à renvoyer
     */
    public void doDelete(String location, Response response){
        String info = "<H1>";
        File fileToDelete = new File(pwd + location);
        if(!fileToDelete.exists()){
            System.out.println("echec");
            response.setResponseCode(404);
            response.setBody("Error 404 : File Not Found".getBytes());
            return;
        } else
        if (fileToDelete.delete()) {
            response.setResponseCode(200);
            info += "Fichier supprimé: " + fileToDelete.getName();
            System.out.println("supprimé");
        } else {
            info += "Echec de la suppression.";
            System.out.println("echec");
        }

        info += "</H1>";
        byte[] data = info.getBytes();
        response.setBody(data);
        return ;
    }

}
