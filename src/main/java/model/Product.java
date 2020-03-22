package model;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static model.User.Currency.RUB;
import static model.User.Currency.USD;

public class Product {

    private final int id;
    private final String name;
    private final Map<User.Currency, String> prices;

    public Product(Document doc) {
        this(doc.getInteger("id"),
                doc.getString("name"),
                new HashMap<User.Currency, String>() {{
                    put(USD, doc.getString(USD.toString()));
                    put(RUB, doc.getString(RUB.toString()));
                }});
    }

    public Product(int id, String name, Map<User.Currency, String> prices) {
        this.id = id;
        this.name = name;
        this.prices = prices;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<User.Currency, String> getPrices() {
        return prices;
    }

    public Document toDocument() {
        return new Document("id", id).append("name", name)
                .append(USD.toString(), prices.get(USD)).append(RUB.toString(), prices.get(RUB));
    }

    public String toString(User.Currency currency) {
        return "Product: {\n" +
                "\tid: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\t" + currency.toString() + ": " + prices.get(currency) + "\n" +
                "}";
    }

    @Override
    public String toString() {
        return "Product: {\n" +
                "\tid: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\t" + RUB.toString() + ": " + prices.get(RUB) + ",\n" +
                "\t" + USD.toString() + ": " + prices.get(USD) + "\n" +
                "}";
    }
}
