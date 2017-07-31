package useability;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by AndyRadulescu on 6/3/2017.
 */
public class Message implements Serializable {
    private int action;
    private int direction;
    private double rect1YPos;
    private double rect2YPos;
    private double ballXpos;
    private double ballYpos;
    private double height;
    private double width;
    private double ballSpeed;
    private double mousePos;

    /**
     * Sends to the server all the attributes of the game to be stored on server.
     *
     * @param action
     * @param rect1YPos
     * @param rect2YPos
     * @param ballXpos
     * @param ballYpos
     * @param height
     * @param width
     * @param ballSpeed
     */
    public Message(int action, double rect1YPos, double rect2YPos, double ballXpos, double ballYpos, double height, double width, double ballSpeed) {
        this.action = action;
        this.direction = direction;
        this.rect1YPos = rect1YPos;
        this.rect2YPos = rect2YPos;
        this.ballXpos = ballXpos;
        this.ballYpos = ballYpos;
        this.height = height;
        this.width = width;
        this.ballSpeed = ballSpeed;
    }

    /**
     * This is used to set the client.
     *
     * @param action
     */
    public Message(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public double getRect1YPos() {
        return rect1YPos;
    }

    public void setRect1YPos(double rect1YPos) {
        this.rect1YPos = rect1YPos;
    }

    public double getRect2YPos() {
        return rect2YPos;
    }

    public void setRect2YPos(double rect2YPos) {
        this.rect2YPos = rect2YPos;
    }

    public double getBallXpos() {
        return ballXpos;
    }

    public void setBallXpos(double ballXpos) {
        this.ballXpos = ballXpos;
    }

    public double getBallYpos() {
        return ballYpos;
    }

    public void setBallYpos(double ballYpos) {
        this.ballYpos = ballYpos;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getBallSpeed() {
        return ballSpeed;
    }

    public void setBallSpeed(double ballSpeed) {
        this.ballSpeed = ballSpeed;
    }

    /**
     * @returns a string containing all the attributes of the current game.
     */
    public String serializedAllMessage() {
        DecimalFormat df = new DecimalFormat("#.##");
        return action + ";" + df.format(rect1YPos) + ";" + df.format(rect2YPos) + ";" + df.format(ballXpos) + ";" + df.format(ballYpos) +
                ";" + df.format(height) + ";" + df.format(width) + ";" + df.format(ballSpeed) + ";";
    }

    /**
     * @returns a message to ping the server.
     */
    public String serializedPing() {
        return action + ";" + "pinging the server , answer to continue!" + ";";
    }

    /**
     * @returns a string to be sent to sent the client .
     */
    public String serializedClient() {
        return action + ";";
    }

    @Override
    public String toString() {
        return "Message{" +
                "action=" + action +
                ", rect1YPos=" + rect1YPos +
                ", rect2YPos=" + rect2YPos +
                ", ballXpos=" + ballXpos +
                ", ballYpos=" + ballYpos +
                ", height=" + height +
                ", width=" + width +
                ", ballSpeed=" + ballSpeed +
                '}';
    }
}
