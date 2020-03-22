import com.mongodb.rx.client.Success;
import db.ReactiveMongoDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.internal.util.ScalarSynchronousObservable;
import server.Server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerTest {

    ReactiveMongoDriver mongoDriver;
    Server server;

    @Before
    public void before() {
        mongoDriver = Mockito.mock(ReactiveMongoDriver.class);
        Mockito.when(mongoDriver.addProduct(Mockito.any())).thenReturn(Success.SUCCESS);
        Mockito.when(mongoDriver.addUser(Mockito.any())).thenReturn(Success.SUCCESS);
        server = new Server(mongoDriver);
    }

    @Test
    public void testAddUser() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        params.put("name", Collections.singletonList("Test"));
        params.put("cur", Collections.singletonList("rub"));
        rx.Observable<String> res = server.addUser(params);
        Assert.assertEquals("New user:\n" +
                        "User: {\n" +
                        "\tid: 1,\n" +
                        "\tname: Test,\n" +
                        "\tcurrency: RUB\n" +
                        "}",
                ((ScalarSynchronousObservable) res).get().toString());
    }

    @Test
    public void testAddUserMissingParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        rx.Observable<String> res = server.addUser(params);
        Assert.assertEquals("Please add params: name, cur", ((ScalarSynchronousObservable) res).get());
    }

    @Test
    public void testAddProduct() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        params.put("name", Collections.singletonList("Test"));
        params.put("rub", Collections.singletonList("25"));
        params.put("usd", Collections.singletonList("12"));
        rx.Observable<String> res = server.addProduct(params);
        Assert.assertEquals("New product:\n" +
                        "Product: {\n" +
                        "\tid: 1,\n" +
                        "\tname: Test,\n" +
                        "\tRUB: 25,\n" +
                        "\tUSD: 12\n" +
                        "}",
                ((ScalarSynchronousObservable) res).get().toString());
    }

    @Test
    public void testAddProductMissingParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        rx.Observable<String> res = server.addProduct(params);
        Assert.assertEquals("Please add params: name, usd, rub", ((ScalarSynchronousObservable) res).get());
    }
}
