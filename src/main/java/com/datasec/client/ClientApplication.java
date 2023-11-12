package com.datasec.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

import com.datasec.remoteInterface.PrinterCommandsInterface;
import com.datasec.server.PrinterServer;
import com.datasec.server.Session;
import com.datasec.utils.SystemException;
import com.datasec.utils.enums.*;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientApplication {
    private static boolean isLoginEnabled = true;
    private static JFrame frame;
    private static JPanel loginPanel;
    private static JPanel printerPanel;
    private static JComboBox<String> printerComboBox;
    private static JTextArea logTextArea;
    private static JButton printButton;
    private static JButton showQueueButton;
    private static JButton startButton;
    private static JButton stopButton;
    private static JButton restartButton;
    private static JButton readParameterButton;
    private static JButton setParameterButton;
    private static JButton readAllParameterButton;
    private static JButton topQueueButton;
    private static JButton statusButton;
    private static JButton logOutButton;

    private static JTextField fileNameTextField;
    private static JFormattedTextField jobNumberTextField;

    // RMI Server interface, adjust the class and methods accordingly
    private static PrinterCommandsInterface server;

    private static String sessionIdUser;

    public static void main(String[] args) {
        try {
            server = (PrinterCommandsInterface) Naming.lookup("rmi://localhost:4002/printerServerName1");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        frame = new JFrame("Printer App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());

        createLoginPanel();
        createPrinterPanel();
        createLogArea();

        frame.add(loginPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JPanel loginFieldPanel = new JPanel();
        loginFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 150)); // Use FlowLayout for smaller text fields

        JLabel loginLabel = new JLabel("Login:");
        JTextField loginField = new JTextField(15);
        loginField.setPreferredSize(new Dimension(150, 40)); // Adjust the size
        loginField.setFont(new Font("Arial", Font.PLAIN, 14)); // Adjust the font size
        loginFieldPanel.add(loginLabel);
        loginFieldPanel.add(loginField);

        JPanel passwordFieldPanel = new JPanel();
        passwordFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 26, 0));

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(150, 40)); // Adjust the size
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14)); // Adjust the font size
        passwordFieldPanel.add(passwordLabel);
        passwordFieldPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoginEnabled) {
                    String username = loginField.getText();
                    char[] password = passwordField.getPassword();
                    try {
                        if (isLoginValid(username, password)) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    frame.remove(loginPanel);
                                    frame.add(printerPanel, BorderLayout.CENTER);
                                    frame.revalidate();
                                    frame.repaint();
                                    printerComboBox.setSelectedIndex(0);
                                    logTextArea.setText("");
                                    logTextArea.append(username + ": login successful\n\n");
                                }
                            });
                        } else {
                            showLoginFailedDialog();
                        }
                    } catch (SystemException sysEx) {
                        if (sysEx.getErrorCode().equals("20")) {
                            showLoginFailedDialog();
                        }

                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Clear the password field for security
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "10 seconds countdown not finished. Please try again in a while.", "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            }
        });

        loginPanel.add(loginFieldPanel);
        loginPanel.add(passwordFieldPanel);
        loginPanel.add(loginButton);

        loginPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    }

    private static boolean isLoginValid(String username, char[] password) throws RemoteException {

        sessionIdUser = server.authenticate(username, String.valueOf(password));
        return Optional.ofNullable(sessionIdUser).isPresent();


    }

    private static void createPrinterPanel() {
        printerPanel = new JPanel();
        printerPanel.setLayout(new BoxLayout(printerPanel, BoxLayout.Y_AXIS));

        printerComboBox = new JComboBox<>(new String[]{"printer1", "printer2", "printer3", "printer4"});
        printerComboBox.setSelectedIndex(0);

        JPanel readParameterPanel = new JPanel();
        readParameterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JComboBox<PrinterParamsEnum> readParameterComboBox = new JComboBox<>(PrinterParamsEnum.values());
        readParameterPanel.add(readParameterComboBox);


        JPanel setParameterPanel = new JPanel();
        setParameterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JComboBox<PrinterParamsEnum> setParameterComboBox = new JComboBox<>(PrinterParamsEnum.values());

        JLabel secondDropdownLabel = new JLabel("Select Value:");

        JComboBox<Object> setParameterValueComboBox = new JComboBox<>();


        JPanel printPanel = new JPanel();
        printPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        fileNameTextField = new JTextField(25);

        JPanel topQueuePanel = new JPanel();
        topQueuePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        jobNumberTextField = new JFormattedTextField(formatter);
        jobNumberTextField.setColumns(4);


        printerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setParameterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterParamsEnum selectedParameter = (PrinterParamsEnum) setParameterComboBox.getSelectedItem();
                setParameterValueComboBox.removeAllItems();

                switch (selectedParameter) {
                    case COLOUR_TYPE_PRINT:
                        setParameterValueComboBox.setModel(new DefaultComboBoxModel<>(ColourTypePrintValueEnum.values()));
                        break;
                    case PRINT_QUALITY:
                        setParameterValueComboBox.setModel(new DefaultComboBoxModel<>(PrintQualityValueEnum.values()));
                        break;
                    case PAGE_SIZE:
                        setParameterValueComboBox.setModel(new DefaultComboBoxModel<>(PageSizeValueEnum.values()));
                        break;
                    case IS_PAGE_ORIENTATION_VERTICAL:
                    case IS_DOUBLE_SIDED:
                        setParameterValueComboBox.setModel(new DefaultComboBoxModel<>(new Boolean[]{true, false}));
                        break;
                    case INK_LEVEL:
                        setParameterValueComboBox.setModel(new DefaultComboBoxModel<>(InkLevelValueEnum.values()));
                        break;
                }
            }
        });

        printButton = new JButton("Print");
        showQueueButton = new JButton("Show Queue");
        topQueueButton = new JButton("Top Queue");
        readParameterButton = new JButton("Read Parameter");
        setParameterButton = new JButton("Set Parameter");
        readAllParameterButton = new JButton("Read All Parameters");
        statusButton = new JButton("Status Printer");
        startButton = new JButton("Start Printer");
        stopButton = new JButton("Stop Printer");
        restartButton = new JButton("Restart Printer");

        logOutButton = new JButton("Log Out");

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileNameTextField.getText();
                if (fileName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a file name.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (containsValidFileName(fileName)) {
                    String selectedPrinter = printerComboBox.getSelectedItem().toString();
                    try {
                        logTextArea.append(server.print(fileName, selectedPrinter, sessionIdUser) + "\n");
                    } catch (SystemException sysEx) {
                        if (sysEx.getErrorCode().equals("10")) {
                            goBackToLoginPage();
                            logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                        }
                    } catch (RemoteException ex) {
                        logTextArea.append("Error printing: " + ex.getMessage() + "\n");
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a VALID file name (name with no special characters + extension).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        showQueueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.queue(selectedPrinter, sessionIdUser) + "\n");
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error showing queue: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        topQueueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer jobNumber = null;
                try {
                    jobNumber = Integer.parseInt(jobNumberTextField.getText());
                } catch (NumberFormatException numEx) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid job number", "Error", JOptionPane.ERROR_MESSAGE);
                    jobNumberTextField.setText("");
                }
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.topQueue(selectedPrinter, jobNumber, sessionIdUser) + "\n");
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error moving to top queue: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        statusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.status(selectedPrinter, sessionIdUser) + "\n");
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error showing status: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.start(selectedPrinter, sessionIdUser) + "\n");
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error starting printer: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.stop(selectedPrinter, sessionIdUser) + "\n");
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error stopping printer: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.restart(selectedPrinter, sessionIdUser) + "\n");
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error restarting printer: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        readParameterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                String selectedParameter = readParameterComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.readConfig(selectedPrinter, selectedParameter, sessionIdUser) + "\n");
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error restarting printer: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        readAllParameterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.readAllConfigs(selectedPrinter, sessionIdUser) + "\n");
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error showing all parameters: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        setParameterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedParameter = setParameterComboBox.getSelectedItem().toString();
                String selectedValue = (String) setParameterValueComboBox.getSelectedItem().toString();
                String selectedPrinter = printerComboBox.getSelectedItem().toString();
                try {
                    logTextArea.append(server.setConfig(selectedPrinter, selectedParameter, selectedValue, sessionIdUser) + "\n");
                    updatePrinterActions();
                } catch (SystemException sysEx) {
                    if (sysEx.getErrorCode().equals("10")) {
                        goBackToLoginPage();
                        logTextArea.append("SESSION ERROR TIMEOUT, login again \n");
                    }
                } catch (Exception ex) {
                    logTextArea.append("Error restarting printer: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                goBackToLoginPage();

                try {
                    logTextArea.append(server.logOut(sessionIdUser));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                sessionIdUser = null;


            }
        });


        printerPanel.add(printerComboBox);

        printPanel.add(fileNameTextField);
        printPanel.add(printButton);
        printerPanel.add(printPanel);

        printerPanel.add(showQueueButton);

        topQueuePanel.add(jobNumberTextField);
        topQueuePanel.add(topQueueButton);

        printerPanel.add(topQueuePanel);

        readParameterPanel.add(readParameterButton);

        setParameterPanel.add(setParameterComboBox);
        setParameterPanel.add(secondDropdownLabel);
        setParameterPanel.add(setParameterValueComboBox);
        setParameterPanel.add(setParameterButton);

        printerPanel.add(readParameterPanel);
        printerPanel.add(readAllParameterButton);
        printerPanel.add(setParameterPanel);
        printerPanel.add(statusButton);
        printerPanel.add(startButton);
        printerPanel.add(stopButton);
        printerPanel.add(restartButton);
        printerPanel.add(logOutButton);
    }

    private static void updatePrinterActions() throws RemoteException {
        String selectedPrinter = printerComboBox.getSelectedItem().toString();

        if (server.isPrinterRunning(selectedPrinter)) {
            printButton.setEnabled(true);
            showQueueButton.setEnabled(true);
            topQueueButton.setEnabled(true);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
            readParameterButton.setEnabled(true);
            setParameterButton.setEnabled(true);
            statusButton.setEnabled(true);
            readAllParameterButton.setEnabled(true);

        } else {
            printButton.setEnabled(false);
            showQueueButton.setEnabled(false);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            restartButton.setEnabled(false);
            readParameterButton.setEnabled(false);
            setParameterButton.setEnabled(false);
            topQueueButton.setEnabled(false);
            statusButton.setEnabled(false);
            readAllParameterButton.setEnabled(false);
        }
    }

    private static boolean containsValidFileName(String input) {
        String regex = "^[\\w.-]+\\.[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    private static void createLogArea() {
        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(scrollPane, BorderLayout.SOUTH);

    }

    private static void showLoginFailedDialog() {
        isLoginEnabled = false;
        Timer timer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLoginEnabled = true;
                ((Timer) e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();

        JOptionPane.showMessageDialog(frame, "Login failed. Please wait 10 seconds before trying again.", "Error", JOptionPane.ERROR_MESSAGE);
    }


    private static void goBackToLoginPage() {

        JFrame newFrame = new JFrame("Printer App");
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(800, 800);
        newFrame.setLayout(new BorderLayout());

        createLoginPanel();

        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        newFrame.add(scrollPane, BorderLayout.SOUTH);

        newFrame.add(loginPanel, BorderLayout.CENTER);
        newFrame.setVisible(true);
        newFrame.revalidate();
        newFrame.repaint();

        frame.dispose();

        frame = newFrame;
    }
}
