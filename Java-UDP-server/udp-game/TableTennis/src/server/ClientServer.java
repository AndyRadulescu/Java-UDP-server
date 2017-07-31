package server;

import useability.Message;
import useability.Values;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AndyRadulescu on 6/3/2017.
 */
public class ClientServer implements Values {
    /**
     * Sends the message to the server.
     *
     * @param message
     * @param clientSocket
     * @throws IOException
     */
    public static void sendClient1(Message message, DatagramSocket clientSocket) throws IOException {
        System.out.println(message);
        String srlMessage = message.serializedAllMessage();
        //  System.out.println(srlMessage);
        //  System.out.println("[UDP Client All packet] " + srlMessage);
        String encryptedSrlMessage = new Encryption().xorMessage(srlMessage, key);

        byte[] sendData = new byte[srlMessage.length()];
        sendData = encryptedSrlMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), PORT);
        clientSocket.send(sendPacket);
    }

    public static void sendClient2(Message message, DatagramSocket clientSocket) throws IOException {
        System.out.println(message);
        String srlMessage = message.serializedClient2Message();
        //  System.out.println(srlMessage);
        //  System.out.println("[UDP Client All packet] " + srlMessage);
        String encryptedSrlMessage = new Encryption().xorMessage(srlMessage, key);

        byte[] sendData = new byte[srlMessage.length()];
        sendData = encryptedSrlMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), PORT);
        clientSocket.send(sendPacket);
    }


    /**
     * Sends the message to the server.
     *
     * @param message
     * @param clientSocket
     * @throws IOException
     */
    public static void setClient(Message message, DatagramSocket clientSocket) throws IOException {
        String srlMessage = message.serializedClient();
        //   System.out.println("[UDP Client] " + srlMessage);
        String encryptedSrlMessage = new Encryption().xorMessage(srlMessage, key);

        byte[] sendData = new byte[srlMessage.length()];
        sendData = encryptedSrlMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), PORT);
        clientSocket.send(sendPacket);
    }

    /**
     * Sends the message to the server.
     *
     * @param message
     * @param clientSocket
     * @throws IOException
     */
    public static void ping(Message message, DatagramSocket clientSocket) throws IOException {
        String srlMessage = message.serializedPing();
        //  System.out.println("[UDP Client] " + srlMessage);
        String encryptedSrlMessage = new Encryption().xorMessage(srlMessage, key);

        byte[] sendData = new byte[srlMessage.length()];
        sendData = encryptedSrlMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), PORT);
        clientSocket.send(sendPacket);
    }

    /**
     * Receives the message to the server.
     *
     * @param clientSocket
     * @return
     * @throws IOException
     */
    public static String receivePacket(DatagramSocket clientSocket) throws IOException {
        int dataLenght = 200;
        byte[] receiveData = new byte[dataLenght];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        //  System.out.println("[UDP Server] it arrives ,waiting for server response...");
        clientSocket.setSoTimeout(5000);
        clientSocket.receive(receivePacket);

        String encryptedServerMessage = new String(receivePacket.getData());
        encryptedServerMessage = encryptedServerMessage.trim();
        String serverMessage = new Encryption().xorMessage(encryptedServerMessage, key);

        //  System.out.println("[UDP Server] message : " + serverMessage + " , from ip address : " + receivePacket.getAddress() + " , on port : " + receivePacket.getPort());
        return serverMessage;
    }
}
