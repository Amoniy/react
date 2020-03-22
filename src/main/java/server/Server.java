package server;

import com.mongodb.rx.client.Success;
import db.ReactiveMongoDriver;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Product;
import model.User;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static model.User.Currency.RUB;
import static model.User.Currency.USD;

public class Server {

    private final static List<String> ADD_USER_PARAMS = Arrays.asList("id", "name", "cur");
    private final static List<String> ADD_PRODUCT_PARAMS = Arrays.asList("id", "name", "usd", "rub");
    private final static List<String> GET_PRODUCTS_PARAMS = Collections.singletonList("user_id");

    ReactiveMongoDriver mongoDriver;

    public Server(ReactiveMongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(8080).start((request, response) -> {
            String method = request.getDecodedPath().substring(1);
            Map<String, List<String>> params = request.getQueryParameters();
            if ("addUser".equals(method)) {
                return response.writeString(addUser(params));
            }
            if ("getUsers".equals(method)) {
                return response.writeString(getUsers(params));
            }
            if ("addProduct".equals(method)) {
                return response.writeString(addProduct(params));
            }
            if ("getProducts".equals(method)) {
                return response.writeString(getProducts(params));
            }
            return response.writeString(Observable.just("404. Try 'addUser' 'getUsers' 'addProduct' 'getProducts'"));
        }).awaitShutdown();
    }

    public Observable<String> addUser(Map<String, List<String>> params) {
        String validation = validate(params, ADD_USER_PARAMS);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get("id").get(0));
        String name = params.get("name").get(0);
        String currency = params.get("cur").get(0);
        User user = new User(id, name, Enum.valueOf(User.Currency.class, currency.toUpperCase()));
        if (mongoDriver.addUser(user) == Success.SUCCESS) {
            return Observable.just("New user:\n" + user.toString());
        } else {
            return Observable.just("Error");
        }
    }

    public Observable<String> getUsers(Map<String, List<String>> params) {
        return mongoDriver.getUsers();
    }

    public Observable<String> addProduct(Map<String, List<String>> params) {
        String validation = validate(params, ADD_PRODUCT_PARAMS);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get("id").get(0));
        String name = params.get("name").get(0);
        String usd = params.get("usd").get(0);
        String rub = params.get("rub").get(0);
        Product product = new Product(id, name,
                new HashMap<model.User.Currency, String>() {{
                    put(USD, usd);
                    put(RUB, rub);
                }});
        if (mongoDriver.addProduct(product) == Success.SUCCESS) {
            return Observable.just("New product:\n" + product.toString());
        } else {
            return Observable.just("Error");
        }
    }

    public Observable<String> getProducts(Map<String, List<String>> params) {
        String validation = validate(params, GET_PRODUCTS_PARAMS);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get("user_id").get(0));
        return mongoDriver.getProducts(id);
    }

    private String validate(Map<String, List<String>> params, List<String> expectedParams) {
        List<String> missingParams = expectedParams.stream().filter(param -> !params.containsKey(param)).collect(Collectors.toList());
        if (missingParams.isEmpty()) {
            return "";
        }
        return "Please add params: " + String.join(", ", missingParams);
    }
}
