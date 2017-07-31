package application;

import java.net.SocketException;

/**
 * Created by AndyRadulescu on 5/22/2017.
 */
public class Main {
    public static void main(String[] args) throws SocketException {
        Thread t1 = new Thread(new TableTennis());
        t1.start();
    }
}