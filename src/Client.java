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

    public static void main(String args[])
            throws Exception
    {

        // Create client socket
        Socket s = new Socket("localhost", 888);

        // to send data to the server
        DataOutputStream dos
                = new DataOutputStream(
                s.getOutputStream());

        // to read data coming from the server
        BufferedReader br
                = new BufferedReader(
                new InputStreamReader(
                        s.getInputStream()));

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
                    while (true) {

                        Thread.sleep(100);

                        // go through list of send queue:
                        for (String eachString : new ArrayList<>(sendQueue)) {
                            sendQueue.remove(0);
                            println(eachString + " is now sent");
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

                    dos.close();
                    br.close();
                    s.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
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
                        receiveQueue.add(br.readLine());
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
