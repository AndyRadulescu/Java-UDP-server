package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by AndyRadulescu on 5/31/2017.
 */
public class UDPServer implements Values, Sizes {

    private int actualPortNumber;
    private DatagramSocket socket = null;
    private volatile boolean isRunning = true;
    private int direction;
    private double rect1YPos = startX;
    private double rect2YPos = startX;
    private double ballXpos = canvasSizeX / 2;
    private double ballYpos = canvasSizeY / 2;
    private double height = 0;
    private double width = 1;
    private double ballSpeed = 1;
    private InetAddress[] IPAddress;
    private int ports[];

    public static void main(String[] args) {
        new UDPServer().startServer();
    }

    /**
     * Adding ports to the ports list.
     *
     * @param receivePacket
     */
    public void addingPorts(DatagramPacket receivePacket) {
        int clientPort = receivePacket.getPort();
        if (ports[0] == 0) {
            ports[0] = clientPort;
        } else if (ports[1] == 0 && clientPort != ports[0]) {
            ports[1] = receivePacket.getPort();
        }
    }

    /**
     * The server itself , runs infinitely. Uses UDP protocol connection .
     */
    private void startServer() {
        try {
            IPAddress = new InetAddress[2];
            ports = new int[2];
            socket = new DatagramSocket(PORT);
            System.out.println("Starting the UDP server.. " + PORT);

            System.out.println("Waiting for incoming messages on port : " + PORT);
            while (isRunning) {
                byte[] receiveData = new byte[DATALENGHT];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                addingPorts(receivePacket);

                InetAddress IPAddress2 = receivePacket.getAddress();
                actualPortNumber = receivePacket.getPort();
                String encryptedMessage = new String(receivePacket.getData());
                encryptedMessage = encryptedMessage.trim();
                String decryptedMessage = Encryption.xorMessage(encryptedMessage, key);
                // System.out.println(rect2YPos);
                System.out.println("[UDP Server] the  : " + decryptedMessage + " , from ip address : " + IPAddress2 + ", on port :" + ports[0] + " " + ports[1] + " " + actualPortNumber);

                byte[] sendData = new byte[decryptedMessage.length()];
                String encryptedResponseMessage = Encryption.xorMessage(chooseOperation(decryptedMessage), key);
                sendData = encryptedResponseMessage.getBytes();

                if (ports[0] != 0) {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress2, ports[0]);
                    socket.send(sendPacket);
                }
                if (ports[1] != 0) {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress2, ports[1]);
                    socket.send(sendPacket);
                }
            }
        } catch (SocketException e) {
            System.out.println(PORT + " already in use");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Chooses the operation to do, sent from the client(the client request).
     *
     * @param message
     * @return the string to be sent via socket.
     */
    private String chooseOperation(String message) {
        String parts[] = message.split(";");
        int action = Integer.parseInt(parts[0]);
        switch (action) {
            case PING: {
                Message m = new Message(PING);
                return m.serializedPing();
            }
            case SETCLIENT: {
                Message m = null;
                if (actualPortNumber == ports[0]) {
                    m = new Message(SETCLIENT, -1);
                } else if (actualPortNumber == ports[1]) {
                    m = new Message(SETCLIENT, -2);
                }
                return m.serializedClient();
            }
            case FIRSTSET: {
                Message msg1 = new Message(action, rect1YPos, rect2YPos, ballXpos, ballYpos, height, width, ballSpeed);
                return msg1.serializedAllMessage();
            }
            case SETALL: {
                if (actualPortNumber == ports[1]) {
                    this.rect1YPos = Double.parseDouble(parts[1]);
                }
                if (actualPortNumber == ports[0]) {
                    this.rect2YPos = Double.parseDouble(parts[2]);
                    System.out.println(actualPortNumber);
                }
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
