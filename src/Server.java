// Server2 class that
// receives data and sends data 
  
import java.io.*;
        import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class Server {

    static ArrayList<AccountObject> accounts = new ArrayList<>();

    static ArrayList<String> sendQueue = new ArrayList<>();
    static ArrayList<String> receiveQueue = new ArrayList<>();

    public static void main(String args[])
            throws Exception
    {
        addAccounts();

        // Create server Socket 
        ServerSocket ss = new ServerSocket(1978);

        // connect it to client socket
        Socket s = ss.accept();
        System.out.println("Connection established");

        // to send data to the client
        PrintStream ps
                = new PrintStream(s.getOutputStream());

        // to read data coming from the client
        BufferedReader br
                = new BufferedReader(
                new InputStreamReader(
                        s.getInputStream()));

        startScannerIn();
        startScannerOut(br);

        // to read data from the keyboard
//        BufferedReader kb
//                = new BufferedReader(
//                new InputStreamReader(System.in));

        // server executes continuously
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    // read from client
                    mainloop: while (true) {

                    Thread.sleep(100);

                        for (String eachString : new ArrayList<>(receiveQueue)) {
                            receiveQueue.remove(0);
                            handleMessage(eachString);
                        }

                        // go through list of send queue:
                        for (String eachString : new ArrayList<>(sendQueue)) {
                            sendQueue.remove(0);
//                            println(eachString + " is now sent");
                            // send to the server
                            ps.println(eachString);
                            if (eachString.toLowerCase().equals("closeconn")) break mainloop;
                        }

//                str1 = kb.readLine();
                        // send to client
//                ps.println(str1);
                    }

                    // close connection
                    ps.close();
                    br.close();
//            kb.close();
                    ss.close();
                    s.close();

                    // terminate application
                    System.exit(0);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void addAccounts() {
        AccountObject object = new AccountObject("Vigor", "123");
        accounts.add(object);
    }

    static void handleMessage(String message){
        message = message.replace("\n", "");
        println(message);
        String[] splitted = message.split(" ");
        try{
            if (accounts.contains(new AccountObject(splitted[0], splitted[1]))){
                sendQueue.add("Authentication accepted");
            }else{
                sendQueue.add("Authentication denied");
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            sendQueue.add("Authentication denied");
        }
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

