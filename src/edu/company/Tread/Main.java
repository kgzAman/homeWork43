package edu.company.Tread;

import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args)  {
        try {
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // server
    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 8080);
        String msg = "Запускаем сервер по адресу" +
                " http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(),address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("удачно");
        return server;
    }

    private static void initRoutes(HttpServer server){
        server.createContext("/",Main::handle);
        server.createContext("/apps/",Main::handle2);
        server.createContext("/apps/profile",Main::handle3);
    }

// handle
    public static void handle(HttpExchange exchange) throws IOException {
            try (Writer writer =  getWriterFrom(exchange)){
            String method =exchange.getRequestMethod();
            Path path = Path.of("fiels\\text.txt");
            byte[] fileBytes = Files.readAllBytes(path);
            if(Files.exists(path) && !Files.isDirectory(path)){
                exchange.sendResponseHeaders(200,fileBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(fileBytes);
                writeData(writer,exchange);
                writer.flush();}
            }
         catch (IOException e) {
                e.printStackTrace();
            }
    }
//handle for img
    public static void handle2(HttpExchange exchange) throws IOException {
        Path path = Path.of("fiels\\1.jpg");
        byte[] fileBytes = Files.readAllBytes(path);
        OutputStream output = exchange.getResponseBody();
        if (Files.exists(path) && !Files.isDirectory(path)) {
            exchange.sendResponseHeaders(200, fileBytes.length);
            output.write(fileBytes);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }

    public static void handle3(HttpExchange exchange) throws IOException {
        try (Writer writer = getWriterFrom(exchange)) {
            String method = exchange.getRequestMethod();
            Path path = Path.of("fiels\\bg.png");
            byte[] fileBytes = Files.readAllBytes(path);
            if (Files.exists(path) && !Files.isDirectory(path)) {
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(fileBytes);
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public static Writer getWriterFrom(HttpExchange exchange){
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output,false,charset);

    }

    private static BufferedReader getReader(HttpExchange exchange){
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input,charset);
        return new BufferedReader(isr);
    }

    public static void writeData (Writer writer,HttpExchange exchange){
        try {
            BufferedReader reader = getReader(exchange);
            if (!reader.ready()){
                return;
            }
            writer(writer,"Блок данных","");
            reader.lines().forEach(v->writer(writer,"\t",v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writer(Writer writer,String msg,String method){
        String data = String.format("%s: %s%n%n",msg,method);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}