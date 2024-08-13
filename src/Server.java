import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> publishers;
    private ArrayList<ConnectionHandler> subscribers;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    private int port;

    public Server(String port) {
        publishers = new ArrayList<>();
        subscribers = new ArrayList<>();
        done = false;
        this.port = Integer.parseInt(port);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server listening on port " + port);
            server = new ServerSocket(port);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                pool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : publishers) {
                if (ch != null) {
                    ch.shutdown();
                }
            }
            for (ConnectionHandler ch : subscribers) {
                if (ch != null) {
                    ch.shutdown();
                }
            }
        } catch (IOException e) {
            //ignore
        }
    }

    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private int id;
        private String role;
        private String topic;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        public void broadcast(String message, String topic) {
            for (ConnectionHandler ch : subscribers) {
                if (ch != null && ch.topic.equals(topic)) {
                    ch.sendMessage(message);
                }
            }
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                role = in.readLine();
                topic = in.readLine();

                if ("PUBLISHER".equals(role)) {
                    publishers.add(this);
                    id = publishers.size();
                    System.out.println("PUBLISHER-" + this.topic + "-" + this.id + ": connected!");
                } else if ("SUBSCRIBER".equals(role)) {
                    subscribers.add(this);
                    id = subscribers.size();
                    System.out.println("SUBSCRIBER-" + this.topic + "-" + this.id + ": connected!");
                } else {
                    out.println("Invalid role! Disconnecting...");
                    this.shutdown();
                    return;
                }


                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("terminate")) {
                        System.out.println(this.role + "-" + this.id + ": left!");
                        this.shutdown();
                    } else if ("PUBLISHER".equals(role)) {
                        broadcast("PUBLISHER-" + this.topic + "-" + this.id + ": " + message, this.topic);
                    } else {
                        out.println("You are not authorized to publish messages!");
                    }
                }
            } catch (IOException e) {
                this.shutdown();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                // ignore
            } finally {
                if ("PUBLISHER".equals(role)) {
                    publishers.remove(this);
                } else if ("SUBSCRIBER".equals(role)) {
                    subscribers.remove(this);
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(args[0]);
        server.run();
    }
}