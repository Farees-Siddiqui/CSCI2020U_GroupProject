package com.example.csci2020u_groupproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {

    public static String message;
    public static TextArea board = new TextArea();
    public static HBox box = new HBox();
    public static Button exit = new Button("Exit");

    private static class ClientHandler implements Runnable {

        private final Socket clientSock;
        
        /**
        *@param socket used for client connection
        */
        public ClientHandler(Socket socket) {
            clientSock = socket;
        }
        
        /**
        *Function that runs a thread to read in data from the client socket
        */
        public void run() {
            board.setDisable(true); //disables user from editing chatroom board without using 'send'
            BufferedReader inStream = null;
            try {
                inStream = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                FileWriter MesssageToFile = new FileWriter("message_log.txt", true);
                BufferedWriter out = new BufferedWriter(MesssageToFile);

                while ((message = inStream.readLine()) != null) {
                    message += "\n";
                    out.write(message);
                    board.appendText(message);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inStream.close();
                    clientSock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    /**
    * Displays the server side GUI, also handles client connection on the socket
    */
    @Override
    public void start(Stage stage) throws Exception {

        box.setPadding(new Insets(25, 5, 5, 50));
        box.getChildren().addAll(board);
        exit.setTranslateX(50);
        exit.setTranslateY(250);
        exit.setPrefSize(100, 25);

        exit.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        Group root = new Group(box, exit);

        Scene scene = new Scene(root, 575, 300);
        stage.setTitle("ChatBot Server v1.0");
        stage.setScene(scene);
        stage.show();

        new Thread(new Runnable() {
            public void run() {
                ServerSocket serve = null;
                try {
                    serve = new ServerSocket(6666);
                    serve.setReuseAddress(true);
                    System.out.println("Starting server...");
                    System.out.println("wating for client connection...");
                    while (true) {
                        Socket sock = serve.accept();
                        System.out.println("Client is connected " + sock.getInetAddress().getHostAddress()); // this
                        ClientHandler client = new ClientHandler(sock);
                        new Thread(client).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    serve.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /*
    * Main function, runs server GUI
    */
    public static void main(String[] args) {
        launch();
    }
}
