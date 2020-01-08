// Client2 class that
// sends data and receives also

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class Client {

    static ArrayList<String> sendQueue = new ArrayList<>();
    static ArrayList<String> receiveQueue = new ArrayList<>();

    private static boolean closeConnection = false;

    public static void main(String args[])
            throws Exception
    {

        // Create client socket
//        Socket socket = new Socket("172.31.3.217", 1978);
        Socket socket = new Socket("localhost", 1978);

        // to send data to the server
        DataOutputStream dos
                = new DataOutputStream(
                socket.getOutputStream());

        // to read data coming from the server
        BufferedReader br
                = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        // start scanners in separate thread to not stop program
        startScannerIn();
        startScannerOut(br);

        // to read data from the keyboard
//        BufferedReader kb
//                = new BufferedReader(
//                new InputStreamReader(System.in));
//        String receiveString;

        // repeat as long as exit
        // is not typed at client
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainloop:
                    while (!closeConnection) {

                        Thread.sleep(100);

                        // go through list of send queue:
                        for (String eachString : new ArrayList<>(sendQueue)) {
                            sendQueue.remove(0);
//                            println(eachString + " is now sent");
                            // send to the server
                            dos.writeBytes(eachString + "\n");
                            if (eachString.toLowerCase().equals("closeconn")) break mainloop;
                        }

                        for (String eachString : new ArrayList<>(receiveQueue)) {
                            receiveQueue.remove(0);
                            if (eachString != null) {
                                handleMessage(eachString);
                                if (eachString.toLowerCase().equals("closeconn")) break mainloop;
                            }
                        }
                    }

                    System.out.print("Connection closed");

                    dos.close();
                    br.close();
                    socket.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    try{
                        dos.close();
                        br.close();
                        socket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }).start();
    }

    static void handleMessage(String message){
        println(message);
    }

    static void startScannerOut(BufferedReader br){
        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    while (true) {
                        String string = br.readLine();
                        if (string == null)
                            closeConnection = true;
                        receiveQueue.add(string);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        inputThread.start();
    }

    static void startScannerIn(){
        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Scanner scan = new Scanner(System.in);
                while (true) {
                    sendQueue.add(scan.nextLine());
                }
            }
        });

        inputThread.start();
    }

    static void println(String s){
        System.out.println(s);
    }
}
