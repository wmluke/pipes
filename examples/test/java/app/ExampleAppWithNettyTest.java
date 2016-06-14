package app;

import app.models.User;
import com.googlecode.junittoolbox.ParallelRunner;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(ParallelRunner.class)
public class ExampleAppWithNettyTest {


    @BeforeClass
    public static void startApp() {
        RestAssured.port = 9191;

        Thread thread = new Thread(() -> {
            try {
                NettyApp.main(new String[]{Integer.toString(RestAssured.port)});
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

        get("/stream").then().assertThat()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body(containsString("[3,3,5]"));


    }

    @Test
    public void testGetAssets() throws Exception {
        get("/assets").then().contentType(ContentType.HTML).assertThat()
            .statusCode(200)
            .header("Content-type", is("text/html; charset=UTF-8"))
            .body(containsString("<h1>welcome</h1>"));

        get("/assets/main.css").then().assertThat()
            .statusCode(200)
            .header("Content-type", is("text/css; charset=UTF-8"))
            .body(containsString("body {\n" +
                "    background-color: cornflowerblue;\n" +
                "}"));

        get("/assets/styles.css").then().assertThat()
            .statusCode(200)
            .header("Content-type", is("text/css; charset=UTF-8"))
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
            .statusCode(200);

        Map<String, Object> body = new HashMap<>();
        body.put("aaa", "111");
        body.put("bbb", "222");

        given().body(body, ObjectMapperType.JACKSON_2)
            .post("/foo")
            .then()
            .statusCode(200)
            .body(containsString("{\"aaa\":\"111\",\"bbb\":\"222\"}"));

    }

    @Test
    public void testSession() throws Exception {
        SessionFilter sessionFilter = new SessionFilter();

        HashMap<Object, Object> data = new HashMap<>();
        data.put("abc", 123);
        data.put("def", 456);

        given()
            .filter(sessionFilter)
            .formParam("username", "jon.doe@foo.com")
            .formParam("password", "1234Password")
            .post("/session")
            .then()
            .statusCode(400);
    }

    @Test
    public void testUsers() throws Exception {


        given().body(new User().setFirstName("Jon").setLastName("").setEmail("jon.doe"), ObjectMapperType.JACKSON_2)
            .post("/users")
            .then()
            .statusCode(422)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("error.code", is("INVALID_FORMAT"))
            .body("error.message", both(containsString("may not be empty")).and(containsString("not a well-formed email address")))
            .body("error.inputViolations.property", containsInAnyOrder("lastName", "email"))
            .body("error.inputViolations.message", containsInAnyOrder("may not be empty", "not a well-formed email address"));

        given().body(new User().setFirstName("Jon")
            .setLastName("Doe")
            .setEmail("jon.doe@gmail.com"), ObjectMapperType.JACKSON_2)
            .post("/users")
            .then()
            .statusCode(201)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body.id", is(1))
            .body("body.email", is("jon.doe@gmail.com"))
            .body("body.firstName", is("Jon"))
            .body("body.lastName", is("Doe"));

        given().body(new User().setFirstName("Jane")
            .setLastName("Doe")
            .setEmail("jane.doe@gmail.com"), ObjectMapperType.JACKSON_2)
            .post("/users")
            .then()
            .statusCode(201)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body.id", is(2))
            .body("body.email", is("jane.doe@gmail.com"))
            .body("body.firstName", is("Jane"))
            .body("body.lastName", is("Doe"));

        get("/users/1").then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body.id", is(1))
            .body("body.email", is("jon.doe@gmail.com"))
            .body("body.firstName", is("Jon"))
            .body("body.lastName", is("Doe"));


        get("/users/2").then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body.id", is(2))
            .body("body.email", is("jane.doe@gmail.com"))
            .body("body.firstName", is("Jane"))
            .body("body.lastName", is("Doe"));

        get("/users").then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body[0].id", is(1))
            .body("body[0].email", is("jon.doe@gmail.com"))
            .body("body[0].firstName", is("Jon"))
            .body("body[0].lastName", is("Doe"))
            .body("body[1].id", is(2))
            .body("body[1].email", is("jane.doe@gmail.com"))
            .body("body[1].firstName", is("Jane"))
            .body("body[1].lastName", is("Doe"));

        given().body(new User().setId(2)
            .setFirstName("Mindy")
            .setLastName("Doe")
            .setEmail("jane.doe@gmail.com"), ObjectMapperType.JACKSON_2)
            .put("/users/22")
            .then()
            .statusCode(404)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("error.message", is("Record not found"));

        given().body(new User().setId(22)
            .setFirstName("Mindy")
            .setLastName("Doe")
            .setEmail("jane.doe@gmail.com"), ObjectMapperType.JACKSON_2)
            .put("/users/2")
            .then()
            .statusCode(400)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("error.message", is("Mismatched entity ID"));

        given().body(new User().setId(2)
            .setFirstName("Mindy")
            .setLastName("Doe")
            .setEmail("jane.doe@gmail.com"), ObjectMapperType.JACKSON_2)
            .put("/users/2")
            .then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body", is(true));

        get("/users/2").then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body.id", is(2))
            .body("body.email", is("jane.doe@gmail.com"))
            .body("body.firstName", is("Mindy"))
            .body("body.lastName", is("Doe"));

        delete("/users/1").then()
            .statusCode(200)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("body", is(true));

        get("/users/1").then()
            .statusCode(404)
            .header("Content-type", is("application/json; charset=UTF-8"))
            .body("error.message", is("Record not found"));
    }
}
