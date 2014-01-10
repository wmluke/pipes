package net.bunselmeyer.hitch.examples;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;

public class JettyAppTest {

    public JettyAppTest() {
        startApp();
    }

    private void startApp() {
        Thread thread = new Thread(() -> {
            try {
                JettyApp.main(new String[]{"8080"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    @Test
    public void testRoot() throws Exception {
        get("/").then().contentType(ContentType.HTML).assertThat()
                .statusCode(200)
                .header("Content-type", is("text/html; charset=UTF-8"))
                .cookie("foo", is("bar"))
                .body(containsString("<h1>hello world!</h1>"));

    }
}
