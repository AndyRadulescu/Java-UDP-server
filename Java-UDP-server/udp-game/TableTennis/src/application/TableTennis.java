package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.ClientServer;
import useability.Message;
import useability.Sizes;
import useability.Values;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AndyRadulesscu
 */

public class TableTennis extends Application implements Sizes, Values {
    private Timeline timeline = new Timeline(); // Timeline object creation
    private Direction direction = Direction.RIGHT;
    private double height;
    private double width;
    private double ballSpeed;
    private int score = 0;
    private double rect1Y;
    private double rect2Y;
    private Rectangle rect1 = new Rectangle(rectXsize, rectYsize);
    private Rectangle rect2 = new Rectangle(rectXsize, rectYsize);
    private Circle ball = new Circle(10);
    private Label label;
    private int mousePointer = 0;
    private DatagramSocket clientSocket;
    private double ballX;
    private double ballY;
    private static int client;

    private List<Integer> clients = new ArrayList<>();

    /**
     * Constructs the object of the TableTennis class.
     *
     * @throws SocketException
     */
    public TableTennis() throws SocketException {
        clientSocket = new DatagramSocket();
    }

    /**
     * Creates the content using javaFx.
     *
     * @return
     */
    private Parent createContent() {
        Pane layout = new Pane();// layout creation
        layout.setPrefSize(canvasSizeX, canvasSizeY);
        // this whole section makes a shadow for the rackets
        // to look more realistic
        InnerShadow scoreShadow = new InnerShadow();
        scoreShadow.setOffsetX(3.0f);
        scoreShadow.setOffsetY(3.0f);
        scoreShadow.setColor(Color.color(0.6f, 0.6f, 0.6f));
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0, 0, 0));
        // 2 rectangle creation ,going to be the rackets
        rect1.setTranslateX(rect1X);
        rect1.setTranslateY(rect1Y);
        rect1.setArcHeight(edge);// making the edges smooth
        rect1.setArcWidth(edge);
        rect1.setEffect(ds);
        rect2.setTranslateX(rect2X);
        rect2.setTranslateY(rect2Y);
        rect2.setArcHeight(edge);// making the edges smooth
        rect2.setArcWidth(edge);
        rect2.setEffect(ds);
        // this label object is going to be the score panel
        label = new Label();
        label.setEffect(scoreShadow);
        label.setTranslateX(100);
        label.setTranslateY(0);
        label.setText("Score:" + String.valueOf(score));
        label.setFont(Font.font("Arial", 100));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.LIGHTGREY);
        // setting the game timer and ball movement
        double time = 0.005;
        KeyFrame frame = new KeyFrame(Duration.seconds(time), event -> {

            try {
                //  System.out.println(clients[0] + " " + clients[1]);
                if (client == 1) {
                    Message message = new Message(SETALL, rect2Y, ballX, ballY, height, width, ballSpeed);
                    ClientServer.sendClient1(message, clientSocket);
                    String receivedMessage = ClientServer.receivePacket(clientSocket);
                    setAll(receivedMessage);
                } else if (client == 2) {
                    Message message = new Message(CLIENT2, rect1Y);
                    ClientServer.sendClient2(message, clientSocket);
                    String receivedMessage = ClientServer.receivePacket(clientSocket);
                    setAll(receivedMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(rect1Y + "  " + rect2Y);
            if (client == 1) {
                bounce(rect1, rect2);
                die();
            }

            if (client == 1) {
                switch (direction) {
                    case RIGHT:
                        ballX += width;
                        ballY += height;
                        break;
                    case LEFT:
                        ballX -= width;
                        ballY += height;
                        break;
                }
            }
        });
        timeline.getKeyFrames().add(frame);// setting up the timeline
        timeline.setCycleCount(Timeline.INDEFINITE);
        layout.getChildren().addAll(label, ball, rect1, rect2);
        return layout;
    }

    /**
     * Sets the client .
     *
     * @param receivedMessage
     */
    private void setClient(String receivedMessage) {
        String parts[] = receivedMessage.split(";");
        if (parts[0].compareTo(String.valueOf(SETCLIENT)) == 0) {
            int serverClient = Integer.parseInt(parts[1]);
            boolean ok = true;
            for (Integer client : clients) {
                if (client == serverClient)
                    ok = false;
            }
            if (ok) {
                clients.add(serverClient);
            }
        }

        System.out.println("-----------------------------" + clients);
    }

    /**
     * Set all the attributes of this class with the server attributes.
     *
     * @param message
     */
    private void setAll(String message) {
        String parts[] = message.split(";");
        if (parts[0].compareTo("1") == 0 || parts[0].compareTo("7") == 0) {
            this.rect2.setTranslateY(rect2Y);
            this.rect2Y = Double.parseDouble(parts[2]);

            this.ballX = Double.parseDouble(parts[3]);
            this.ballY = Double.parseDouble(parts[4]);
            this.height = Double.parseDouble(parts[5]);
            this.width = Double.parseDouble(parts[6]);
            this.ballSpeed = Double.parseDouble(parts[7]);
            this.ball.setTranslateX(ballX);
            this.ball.setTranslateY(ballY);

            this.rect1Y = Double.parseDouble(parts[1]);
            this.rect1.setTranslateY(rect1Y);
        }
    }

    /**
     * Detects if the game is lost and restarts it.
     */
    private void die() {
        if (ball.getTranslateX() >= (canvasSizeX - 10) || ball.getTranslateX() <= 10) {
            timeline.stop();
            height = 0;
            width = 1;
            ballSpeed = 1;
            ballX = canvasSizeX / 2;
            ballY = canvasSizeY / 2;
            score = 0;
            label.setText("Score:" + String.valueOf(score));

            startGame();
        }
    }

    /**
     * Bounce method .
     *
     * @param rect1
     * @param rect2
     */
    private void bounce(Rectangle rect1, Rectangle rect2) {
        double value1 = (ball.getTranslateY() - rect1.getTranslateY());
        double value2 = (ball.getTranslateY() - rect2.getTranslateY());
        // testing if the ball is between the top and bottom part of the right
        // racket
        // to make the ball squish effect i made the ball to go 5 pixels
        // underneath the rackets
        if ((ball.getTranslateX() >= canvasSizeX - 25) && (ball.getTranslateY() >= rect2.getTranslateY() - 10
                && ball.getTranslateY() <= rect2.getTranslateY() + 110)) {
            direction = Direction.LEFT;
            calculate(value2);
            if (ballSpeed + Sizes.increaseSpeed < maxBallSpeed) {
                ballSpeed += Sizes.increaseSpeed;
            }
            score += 100;
            label.setText("Score:" + String.valueOf(score));
        }
        // testing if the ball is between the top and bottom part of the left
        // racket
        if ((ball.getTranslateX() <= 25) && (ball.getTranslateY() >= rect1.getTranslateY() - 10
                && ball.getTranslateY() <= rect1.getTranslateY() + 110)) {
            direction = Direction.RIGHT;
            calculate(value1);
            if (ballSpeed + Sizes.increaseSpeed < maxBallSpeed) {
                ballSpeed += Sizes.increaseSpeed;
            }
            score += 100;
            label.setText("Score:" + String.valueOf(score));
        }
        // bounce on the top and bottom walls
        if (ball.getTranslateY() < 10 || ball.getTranslateY() > canvasSizeY) {
            height = -height;
        }
    }

    // calculates how much the ball should go up or down
    // the ball is should move in any direction with 1 pixel
    // this is where its calculated the x and y for the ball that x^2+x^2=1

    /**
     * Calculets the angle of the ball's reflection and sets the height and width according to it.
     *
     * @param value
     */
    private void calculate(double value) {
        double y = (50 - value) / 100 * 2;
        if (y >= 1) {
            y = 0.98;
        }
        if (y <= -1) {
            y = -0.98;
        }
        double x = Math.sqrt(ballSpeed - y * y);
        height = -y;
        width = x;
    }

    /**
     * The effect of how everything looks.
     *
     * @return
     */
    private Blend effect() {
        // two effects are made here, an external shadow and a reflection
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);
        Reflection r = new Reflection();
        r.setFraction(1f);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.f, 0.f, 0.f));
        blend.setTopInput(r);
        blend.setBottomInput(ds);
        return blend;
    }

    /**
     * Starts the game.This is where the ball is instantiated ,it also takes an image inside
     * of the circle object ,to look more realistic.
     */
    private void startGame() {
        Image image = new Image("/image/sphere4.png");
        //      direction = Direction.LEFT;
//        ball.setTranslateX(ballX);
//        ball.setTranslateY(ballY);

        ball.setFill(new ImagePattern(image));
        ball.setEffect(effect());
        timeline.play();
    }

    /**
     * Moves the rackets up and down. First LMB must be pressed .The mouse pointer must be dragged up or down.
     * It depends on the client which rect is moved. The first to sing in will move the right racket and the second will
     * move the left racket.
     *
     * @param scene
     */
    private synchronized void movement(Scene scene) {
        // moving the rackets
        // the mouse will act for the right racket by pressing LMB and dragging
        // up or down
        scene.setOnMousePressed(e -> mousePointer = (int) e.getSceneY());
        scene.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                if (e.getSceneY() < mousePointer) {
                    if (client == 1) {
                        if (e.getSceneY() < canvasSizeY - rectYsize && e.getSceneY() > 0) {
                            rect2Y = e.getSceneY();
                        }
                        if (e.getSceneY() < 0) {
                            rect2Y = 0;
                        }
                    }
                    if (client == 2) {
                        if (e.getSceneY() < canvasSizeY - rectYsize && e.getSceneY() > 0) {
                            rect1Y = e.getSceneY();
                        }
                        if (e.getSceneY() < 0) {
                            rect1Y = 0;
                        }
                    }
                } else {
                    if (client == 1) {
                        if (e.getSceneY() > 0 && e.getSceneY() < canvasSizeY - rectYsize) {
                            rect2Y = e.getSceneY();
                        }
                        if (e.getSceneY() > canvasSizeY) {
                            rect2Y = canvasSizeY - rectYsize;
                        }
                    }
                    if (client == 2) {
                        if (e.getSceneY() > 0 && e.getSceneY() < canvasSizeY - rectYsize) {
                            rect1Y = e.getSceneY();
                        }
                        if (e.getSceneY() > canvasSizeY) {
                            rect1Y = canvasSizeY - rectYsize;
                        }
                    }
                }
                mousePointer = (int) e.getSceneY();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            InetAddress serverIpAddress = InetAddress.getByName(HOST);
            System.out.println("Trying to send to : " + serverIpAddress + " via port : " + PORT);
            Message message = new Message(PING);
            ClientServer.ping(message, clientSocket);
            String receivedMessage = ClientServer.receivePacket(clientSocket);
            if (receivedMessage.compareTo("0;Received! safoddsfsadf sdaf sadf asd;") == 0) {
                if (client == 1) {
                    rect2Y = (canvasSizeY - rectYsize) / 2;
                    rect1Y = (canvasSizeY - rectYsize) / 2;
                    ballX = canvasSizeX / 2;
                    ballY = canvasSizeY / 2;
                    height = 0;
                    width = 1;
                    ballSpeed = 1;
                } else if (client == 2) {
                    Message message2 = new Message(CLIENT2SET);
                    ClientServer.sendClient1(message2, clientSocket);
                    String setAll = ClientServer.receivePacket(clientSocket);
                    setAll(setAll);
                }
                Scene scene = new Scene(createContent());
                movement(scene);// setting up racket movement
                primaryStage.setScene(scene);
                primaryStage.show();
                startGame();
            } else {
                throw new IOException("Server not online ... Couldn't connect to the server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SocketException {
        System.out.println(args[0]);
        client = Integer.parseInt(args[0]);
        launch(args[0]);
    }

    /**
     * It stores the direction left/right.
     */
    private enum Direction {
        LEFT, RIGHT
    }
}
