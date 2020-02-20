import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Client {
    private JTextArea show;
    private JTextArea draft;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
//Main
    public static void main(String[] args) {
        Client client = new Client();
        client.setup();
        Thread readerThread = new Thread(client.new ServerReader());
        readerThread.start();
        client.display();
    }
//Inner Classes
    class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            pw.println(draft.getText());
            pw.flush();
            draft.setText("");
            draft.requestFocus();
        }
    }
    class ServerReader implements Runnable {
        public void run() {
            String msg;
            System.out.println("running");  //
            try {
                while ((msg = br.readLine()) != null) {
                    System.out.println("Read: " + "\"" + msg + "\"");
                    show.append(msg + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("running");  //
        }
    }
//Methods
    private void setup() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            System.out.println("Networking and I/O established successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to setup");
        }
    }
//GUI
    private void display() {
        JFrame frame = new JFrame("Simple Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        show = new JTextArea(15,50);
        show.setLineWrap(true);
        show.setWrapStyleWord(true);
        show.setEditable(false);
        JScrollPane showScroller = new JScrollPane(show);
        showScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        showScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        draft = new JTextArea(5,50);
        draft.setLineWrap(true);
        draft.setWrapStyleWord(true);
        JScrollPane draftScroller = new JScrollPane(draft);
        draftScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        draftScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendListener());
        panel.add(showScroller);
        panel.add(draftScroller);
        panel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}