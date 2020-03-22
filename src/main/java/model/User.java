package model;

import org.bson.Document;

public class User {

    private final int id;
    private final String name;
    private final Currency currency;

    public User(Document doc) {
        this(doc.getInteger("id"),
                doc.getString("name"),
                Enum.valueOf(Currency.class, doc.getString("currency").toUpperCase()));
    }

    public User(int id, String name, Currency currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Document toDocument() {
        return new Document("id", id).append("name", name).append("currency", currency.toString());
    }

    @Override
    public String toString() {
        return "User: {\n" +
                "\tid: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\tcurrency: " + currency.toString() + "\n" +
                "}";
    }

    public enum Currency {
        USD, RUB
    }
}
