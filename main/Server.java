package main;

import main.couche.Physique;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        Physique p = Physique.getInstance();
        p.ReceiveFrame(44419);
    }
}