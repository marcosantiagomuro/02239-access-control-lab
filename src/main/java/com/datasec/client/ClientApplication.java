package com.datasec.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.*;
import com.datasec.remoteInterface.IPrinterServer;
import com.datasec.server.Printer;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class ClientApplication {

    // maybe should create a RMIClient class and implement that here

    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        JFrame frame = new JFrame("Client for printing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel panel = new JPanel();
        JButton btn1 = new JButton("But 1");
        panel.add(btn1);

        JTextArea textArea = new JTextArea();

        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: write to TextArea
            }
        });

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, textArea);
        frame.setVisible(true);

        // also ?
        /*
         * Registry registry = LocateRegistry.getRegistry("localhost", 9999);
         * PrintServer server = (PrintServer) registry.lookup("test");
         */

        IPrinterServer server = (IPrinterServer) Naming.lookup("rmi://localhost:4002/server1");
        System.out.println(server.echo());

        // Scanner sc = new Scanner(System.in);
        // System.out.println("give me a sentence I will convert to uppercase:");
        // String inputString = sc.nextLine();
        // System.out.println(server.writeToUpperCase(inputString));
        server.print("file.txt", "printer1");

        System.out.println(server.readAllConfigs("printer1"));
        server.print("file2.txt", "printer1");

        System.out.println(server.readAllConfigs("printer1"));

        System.out.println(server.readConfig("printer1", "INK_LEVEL"));

        System.out.println(server.setConfig("printer1", "PAGE_SIZE", "letter"));

        System.out.println(server.readAllConfigs("printer1"));
    }
}
