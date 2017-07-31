package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by eradules on 28.07.2017.
 */
public class User extends Encryption implements Runnable, Values {

    private DatagramPacket receivePacket;
    private double rect1YPos;
    private double rect2YPos;
    private double ballXpos;
    private double ballYpos;
    private double height;
    private double width;
    private double ballSpeed;
    private DatagramSocket socket;
    private volatile User[] users = new User[2];

    private volatile boolean isRunning = true;

    public User(DatagramPacket receivePacket, DatagramSocket socket, User[] users) {
        this.receivePacket = receivePacket;
        this.socket = socket;
        this.users = users;
    }

    @Override
    public synchronized void run() {
        while (isRunning) {
            try {
                String encryptedMessage = new String(receivePacket.getData());
                encryptedMessage = encryptedMessage.trim();
                String decryptedMessage = Encryption.xorMessage(encryptedMessage, key);
                String operationChosen = chooseOperation(decryptedMessage);
                for (int i = 0; i < 2; i++) {
                    if (users[i] != null) {
                        byte[] sendData = new byte[decryptedMessage.length()];
                        String encryptedResponseMessage = Encryption.xorMessage(operationChosen, key);
                        sendData = encryptedResponseMessage.getBytes();
                        int port = users[i].receivePacket.getPort();
                        InetAddress IPAddress = users[i].receivePacket.getAddress();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        socket.send(sendPacket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                break;
            }
        }
    }

    /**
     * Chooses the operation to do, sent from the client(the client request).
     *
     * @param message
     * @return the string to be sent via socket.
     */
    private synchronized String chooseOperation(String message) {
        String parts[] = message.split(";");
        int action = Integer.parseInt(parts[0]);
        switch (action) {
            case PING: {
                Message m = new Message(PING);
                return m.serializedPing();
            }
            case CLIENT2SET: {
                Message msg1 = new Message(action, rect1YPos, rect2YPos, ballXpos, ballYpos, height, width, ballSpeed);
                return msg1.serializedAllMessage();
            }
            case SETALL: {
                this.rect1YPos = Double.parseDouble(parts[1]);
                this.rect2YPos = Double.parseDouble(parts[2]);
                this.ballXpos = Double.parseDouble(parts[3]);
                this.ballYpos = Double.parseDouble(parts[4]);
                this.height = Double.parseDouble(parts[5]);
                this.width = Double.parseDouble(parts[6]);
                this.ballSpeed = Double.parseDouble(parts[7]);
                Message msg1 = new Message(action, rect1YPos, rect2YPos, ballXpos, ballYpos, height, width, ballSpeed);
                return msg1.serializedAllMessage();
            }
            default: {
                break;
            }
        }
        return null;
    }
}