package com.example.csci2020u_groupproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.lab10.Server.board;

public class Client extends Application {
    public static AtomicReference<String> uName = new AtomicReference<>("");
    public static AtomicReference<String> message = new AtomicReference<>("");
    public static AtomicBoolean exitStatus = new AtomicBoolean(false);

    public static TextField uNameTxt = new TextField();
    public static TextField messageTxt = new TextField();
    public static Button send = new Button("Send");
    public static Button exit = new Button("Exit");
    public static Button login = new Button("Login");
    public static PrintWriter dout;

    @Override
    public void start(Stage stage) throws IOException {
        login.setTranslateX(50);
        login.setTranslateY(100);

        Label username = new Label("Username: ");
        Label messageLbl = new Label("Message: ");

        exit.setTranslateX(50);
        exit.setTranslateY(135);

        new Thread(new Runnable() {
            public void run() {
                try (Socket sock = new Socket("localhost", 6666)){
                    System.out.println("Connected to server...");
                    //get input from the user to send as a message
                    PrintWriter dout = new PrintWriter(sock.getOutputStream(), true);
                    while(!exitStatus.get()){
                        login.setOnAction(e -> {
                            uName.set(uNameTxt.getText());

                            login.getScene().setRoot(messageScreen(messageLbl, stage, uName, board));
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
        box.getChildren().addAll(username, uNameTxt);
        Group root = new Group(box, login, exit);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Lab 10 Client");
        stage.setScene(scene);
        stage.show();

    }

    private Parent messageScreen(Label messageLbl, Stage stage, AtomicReference<String> uName, TextArea board) {
        stage.setWidth(900);
        board.setDisable(true);
        send.setTranslateX(50);
        send.setTranslateY(100);

        messageLbl.setTranslateX(0);
        messageLbl.setTranslateY(40);

        messageTxt.setTranslateX(0);
        messageTxt.setTranslateY(40);

        exit.setTranslateX(50);
        exit.setTranslateY(135);

        new Thread(new Runnable() {
            public void run() {
                BufferedReader inStream = null;
                try (Socket sock = new Socket("localhost", 6666)){
                    System.out.println("Connected to server...");
                    inStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                    //get input from the user to send as a message
                    PrintWriter dout = new PrintWriter(sock.getOutputStream(), true);
                    while(!exitStatus.get()){
                        send.setOnAction(e -> {
                            message.set(messageTxt.getText());

                            // socket.send the stuff
                            dout.println(uName + ": " + message);
                            board.appendText(uName + ": " + message + "\n");

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
        box.getChildren().addAll(messageLbl, messageTxt, board);
        Group root = new Group(box, send, exit);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("ChatBot Client");
        stage.setScene(scene);
        stage.show();
        return null;
    }

    public static void main(String[] args) {
        launch();
    }
}
