import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

class Server {
    private ArrayList<PrintWriter> clientOutputStreams;
//Main
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 100);
        frame.setVisible(true);
        new Server().run();
    }
//Inner Classes
    class ClientHandler implements Runnable {
        BufferedReader br;
        Socket socket;
        private ClientHandler(Socket s) {
            socket = s;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            String msg;
            try {
                while((msg = br.readLine()) != null) {
                    System.out.println("Read: " + "\"" + msg + "\"");
                    spread(msg);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
//Methods
    void run() {
        clientOutputStreams = new ArrayList<PrintWriter>();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(pw);
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("A new connection established successfully");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void spread(String msg) {
        PrintWriter pw;
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while(it.hasNext()) {
            try {
                pw = it.next();
                pw.println(msg);
                pw.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}