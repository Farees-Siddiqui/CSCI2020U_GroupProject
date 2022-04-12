package com.example.groupproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Client extends Application {
    public static AtomicReference<String> uName = new AtomicReference<>("");
    public static AtomicReference<String> message = new AtomicReference<>("");
    public static AtomicBoolean exitStatus = new AtomicBoolean(false);

    public static TextField uNameTxt = new TextField();
    public static TextField messageTxt = new TextField();
    public static Button send = new Button("Send");
    public static Button exit = new Button("Exit");
    public static PrintWriter dout;

    @Override
    public void start(Stage stage) throws IOException {
        send.setTranslateX(50);
        send.setTranslateY(150);

        Label username = new Label("Username: ");
        Label messageLbl = new Label("Message: ");

        messageLbl.setTranslateX(-220);
        messageLbl.setTranslateY(50);

        messageTxt.setTranslateX(-220);
        messageTxt.setTranslateY(50);

        exit.setTranslateX(50);
        exit.setTranslateY(200);

        new Thread(new Runnable() {
            public void run() {
                try (Socket sock = new Socket("localhost", 6666)){
                    System.out.println("Connected to server...");
                    //get input from the user to send as a message
                    PrintWriter dout = new PrintWriter(sock.getOutputStream(), true);
                    while(!exitStatus.get()){
                        send.setOnAction(e -> {
                            uName.set(uNameTxt.getText());
                            message.set(messageTxt.getText());

                            // socket.send the stuff
                            dout.println(uName + ": " + message);

                        });

                        exit.setOnAction(e -> {
                            exitStatus.set(true);
                            Platform.exit();
                            System.exit(0);
                        });
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                System.out.println("Connection terminated...");
            }
        }).start();



        HBox box = new HBox(5);
        box.setPadding(new Insets(25, 5, 5, 50));
        box.getChildren().addAll(username, uNameTxt, messageLbl, messageTxt);
        Group root = new Group(box, send, exit);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("SimpleBBS Client v1.0");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
