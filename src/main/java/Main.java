import db.ReactiveMongoDriver;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(new ReactiveMongoDriver());
        server.run();
    }
}
