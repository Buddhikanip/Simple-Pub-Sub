import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    private String ip;
    private int port;
    private String role;
    private String topic;

    public Client(String ip, String port, String role, String topic) {
        done = false;
        this.ip = ip;
        this.port = Integer.parseInt(port);
        this.role = role.toUpperCase();
        this.topic = topic.toUpperCase();
    }

    @Override
    public void run() {
        try {
            client = new Socket(ip, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                out.println(role);
                out.println(topic);
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("terminate")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (Exception e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client(args[0], args[1], args[2], args[3]);
        client.run();
    }
}
