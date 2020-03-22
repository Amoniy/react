package db;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import model.Product;
import model.User;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriver {

    private final static String DATABASE_NAME = "shop";

    private MongoClient mongoClient;

    public ReactiveMongoDriver() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
    }

    public Success addUser(User user) {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("users")
                .insertOne(user.toDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public Observable<String> getUsers() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("users").find().toObservable()
                .map(document -> new User(document).toString()).reduce((user1, user2) -> user1 + "\n" + user2);
    }

    public User findUser(Integer id) {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("users")
                .find(eq("id", id)).first().map(User::new).timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public Success addProduct(Product product) {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("products")
                .insertOne(product.toDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public Observable<String> getProducts(Integer id) {
        User.Currency currency = findUser(id).getCurrency();
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("products").find().toObservable()
                .map(document -> new Product(document).toString(currency)).reduce((prod1, prod2) -> prod1 + "\n" + prod2);
    }
}
