package app;

import app.models.User;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.StringContains.containsString;

public class JettyAppTest {

    @BeforeClass
    public static void startApp() {
        Thread thread = new Thread(() -> {
            try {
                JettyApp.main(new String[]{"8080"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        // give the app time to spin up
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testGET() throws Exception {
        get("/").then().contentType(ContentType.HTML).assertThat()
                .statusCode(200)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .cookie("foo", is("bar"))
                .body(containsString("<h1>hello world!</h1>"));

        get("/restricted/foo").then()
                .statusCode(401)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .header("Cookie", nullValue())
                .body(containsString("Restricted Area"));

        get("/locations/usa/ca/san-francisco").then()
                .statusCode(200)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .cookie("foo", is("bar"))
                .body(containsString("<h1>usa, ca, san-francisco</h1>"));

        get("/error").then()
                .statusCode(400)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .cookie("foo", is("bar"))
                .body(containsString("Handled error: Fail!"));

        get("/assets").then().contentType(ContentType.HTML).assertThat()
                .statusCode(200)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .body(containsString("<h1>welcome</h1>"));

        get("/assets/main.css").then().assertThat()
                .statusCode(200)
                .header("Content-type", is("text/css;charset=UTF-8"))
                .body(containsString("body {\n" +
                        "    background-color: cornflowerblue;\n" +
                        "}"));

        get("/assets/styles.css").then().assertThat()
                .statusCode(200)
                .header("Content-type", is("text/css;charset=UTF-8"))
                .body(containsString("body {\n" +
                        "    color: yellow;\n" +
                        "}"));

    }

    @Test
    public void testPOST() throws Exception {

        given().formParam("aaa", "111")
                .formParam("bbb", "222")
                .post("/")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("<p>111, 222</p>"));

        Map<String, Object> body = new HashMap<>();
        body.put("aaa", "111");
        body.put("bbb", "222");

        given().body(body, ObjectMapperType.JACKSON_2)
                .post("/foo")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("{\"aaa\":\"111\",\"bbb\":\"222\"}"));
    }

    @Test
    public void testUsers() throws Exception {

        given().body(new User().setFirstName("Jon").setLastName("Doe"), ObjectMapperType.JACKSON_2)
                .post("/users")
                .then()
                .statusCode(201)
                .header("Content-type", is("application/json;charset=UTF-8"))
                .body(Matchers.containsString("{\"id\":1,\"firstName\":\"Jon\",\"lastName\":\"Doe\"}"));

        given().body(new User().setFirstName("Jane").setLastName("Doe"), ObjectMapperType.JACKSON_2)
                .post("/users")
                .then()
                .statusCode(201)
                .header("Content-type", is("application/json;charset=UTF-8"))
                .body(Matchers.containsString("{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"));

        get("/users/1").then()
                .statusCode(200)
                .header("Content-type", is("application/json;charset=UTF-8"))
                .body(containsString("{\"id\":1,\"firstName\":\"Jon\",\"lastName\":\"Doe\"}"));

        get("/users/2").then()
                .statusCode(200)
                .header("Content-type", is("application/json;charset=UTF-8"))
                .body(containsString("{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"));

        get("/users").then()
                .statusCode(200)
                .header("Content-type", is("application/json;charset=UTF-8"))
                .body(containsString("[{\"id\":1,\"firstName\":\"Jon\",\"lastName\":\"Doe\"},{\"id\":2,\"firstName\":\"Jane\",\"lastName\":\"Doe\"}]"));


    }
}
