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
    private boolean closeConnection = false;
    private String disconnectCommand = "#-disconnect-#";

    public ServerInterface(Socket clientSocket, String servername) {
        this.socket = clientSocket;
        this.servername = servername;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp;
        DataOutputStream out = null;
        Thread receiveThread;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(servername + "\n\r");

            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {


                    try {
                        while (true) {
                            receiveQueue.add(brinp.readLine());
                        }
                    } catch (IOException e) {
                        System.out.print(servername + " ");
                        interrupt();
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    Scanner scan = new Scanner(System.in);
//                    while (true) {
//                        sendQueue.add(scan.nextLine());
//                    }
//                }
//            }).start();
        } catch (IOException e) {
            return;
        }
        String line;
        mainloop:
        while (!this.closeConnection) {
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
                    if (eachString == null)
                        break mainloop;
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
                    if (this.closeConnection) break mainloop;
                }
            } catch (IOException e) {
                closeConnection = true;
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Interrupting");
        requestSocketDisconnect(out);

        try {
            out.close();
            receiveThread.interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }

    private void requestSocketDisconnect(DataOutputStream out) {
        System.out.println("requesting to disconnect");
        try {
            out.writeBytes(this.disconnectCommand + "\n\r");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
                this.sendQueue.add("Authentication denied \nclosing connection");
                this.closeConnection = true;
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            this.sendQueue.add("Authentication denied \nclosing connection");
            this.closeConnection = true;
        }
    }
}
