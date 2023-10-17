package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.*;
import server.IPrintServer;
import java.awt.*;
import java.awt.event.*;

public class ClientApplication {

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

		IPrintServer server = (IPrintServer) Naming.lookup("rmi://localhost:9999/test");
		System.out.println(server.echo());
	}
}
