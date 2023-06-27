package main;

import main.couche.Physique;
import main.couche.Transport;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        Physique p = Physique.getInstance();
        p.ReceiveFrames(44419);

        Transport t = Transport.getInstance();
        byte[] receivedData = t.SendFile();
        System.out.println("Received data : " + receivedData);
    }
}