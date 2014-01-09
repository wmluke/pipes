package net.bunselmeyer.hitch.servlet;

import net.bunselmeyer.hitch.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Predicate;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HttpResponseServletAdapterTest {

    private HttpServletResponse servletResponse;
    private HttpResponse httpResponse;

    @Before
    public void setUp() throws Exception {
        servletResponse = mock(HttpServletResponse.class);
        httpResponse = new HttpResponseServletAdapter(servletResponse);
    }

    @Test
    public void testCookie() throws Exception {

        httpResponse.cookie("foo", "bar", (c) -> {
            c.setPath("/aaa");
            c.setSecure(true);
        });

        verify(servletResponse).addCookie(arg((c) -> {
            return c.getValue().equals("bar") && c.getPath().equals("/aaa") && c.getSecure();
        }));

    }

    @Test
    public void testRedirect() throws Exception {
        httpResponse.redirect("http://foo.bar");
        verify(servletResponse).sendRedirect("http://foo.bar");

    }

    public static <T> T arg(Predicate<T> predicate) {
        return argThat(new ArgumentMatcher<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object argument) {
                return predicate.test((T) argument);
            }
        });
    }

}
