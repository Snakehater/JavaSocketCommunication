import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerInterface extends Thread {
    protected Socket socket;
    protected String servername;

    private ArrayList<AccountObject> accounts = new ArrayList<AccountObject>() {
        {
            add(new AccountObject("Vigor", "123"));
        }
    };

    private ArrayList<String> sendQueue = new ArrayList<>();
    private ArrayList<String> receiveQueue = new ArrayList<>();

    public ServerInterface(Socket clientSocket, String servername) {
        this.socket = clientSocket;
        this.servername = servername;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;

        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(servername + "\n\r");

            BufferedReader finalBrinp = brinp;
            new Thread(new Runnable() {
                @Override
                public void run() {


                    try {
                        while (true) {
                            receiveQueue.add(finalBrinp.readLine());
                        }
                    } catch (IOException e) {
                        System.out.print(servername + " ");
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Scanner scan = new Scanner(System.in);
                    while (true) {
                        sendQueue.add(scan.nextLine());
                    }
                }
            }).start();
        } catch (IOException e) {
            return;
        }
        String line;
        mainloop:
        while (true) {
            try {
                sleep(100);
//                line = brinp.readLine();
//                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
//                    socket.close();
//                    return;
//                } else {
//                    out.writeBytes(line + "\n\r");
//                    out.flush();
//                }
                for (String eachString : new ArrayList<>(this.receiveQueue)) {
                    this.receiveQueue.remove(0);
                    handleMessage(eachString);
                }


                // go through list of send queue:
                for (String eachString : new ArrayList<>(this.sendQueue)) {
                    this.sendQueue.remove(0);
                    System.out.println("Sending " + eachString);
//                            println(eachString + " is now sent");
                    // send to the server
                    out.writeBytes(eachString + "\n\r");
                    out.flush();
                    if (eachString.toLowerCase().equals("closeconn")) break mainloop;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void handleMessage(String message){
        message = message.replace("\n", "");
        System.out.println(message);
        String[] splitted = message.split(" ");
        try{
            if (accounts.contains(new AccountObject(splitted[0], splitted[1]))){
                this.sendQueue.add("Authentication accepted");
            }else{
                this.sendQueue.add("Authentication denied");
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            this.sendQueue.add("Authentication denied");
        }
    }
}
