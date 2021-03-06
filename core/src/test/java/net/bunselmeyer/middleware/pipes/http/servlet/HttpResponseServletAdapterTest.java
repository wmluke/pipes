package net.bunselmeyer.middleware.pipes.http.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.json.StreamModule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class HttpResponseServletAdapterTest {

    private HttpServletResponse servletResponse;
    private HttpResponse httpResponse;

    @Before
    public void setUp() throws Exception {
        servletResponse = mock(HttpServletResponse.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //JsonUtil.configureJsonObjectMapper(objectMapper);
        httpResponse = new HttpResponseServletAdapter(servletResponse, objectMapper);
    }

    @Test
    public void testCookie() throws Exception {

        httpResponse.cookie("foo", "bar", (c) -> {
            c.setPath("/aaa");
            c.setSecure(true);
        });

        verify(servletResponse).addCookie(arg((c) -> {
            return c.getName().equals("foo") &&
                c.getValue().equals("bar") &&
                c.getPath().equals("/aaa") &&
                c.getSecure();
        }));
    }

    @Test
    public void testClearCookie() throws Exception {
        httpResponse.clearCookie("foo");

        verify(servletResponse).addCookie(arg((c) -> {
            return c.getName().equals("foo") &&
                c.getValue().equals("") &&
                c.getMaxAge() == 0;
        }));
    }

    @Test
    public void testRedirect() throws Exception {
        httpResponse.redirect("http://foo.bar");
        verify(servletResponse).sendRedirect("http://foo.bar");
    }

    @Test
    public void testSendOnlyBody() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(0);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.send("foo");

        verify(servletResponse, times(1)).setStatus(200);
        verify(writer).append("foo");
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void testSendStatusBody() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(0);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.send(401, "foo");

        verify(servletResponse, times(1)).setStatus(401);
        verify(writer).append("foo");
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void testSendOnlyStatus() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(0);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.send(401);

        verify(servletResponse).setStatus(401);
        verify(servletResponse, never()).getWriter();
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void testJsonOnlyBody() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(0);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.json("foo");

        verify(servletResponse).setStatus(200);
        verify(servletResponse).setContentType("application/json");
        verify(servletResponse).setCharacterEncoding("UTF-8");
        verify(writer).append("foo");
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void testJsonStatusBody() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(401);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.toJson(401, "foo");

        verify(servletResponse, times(1)).setStatus(401);
        verify(servletResponse).setContentType("application/json");
        verify(servletResponse).setCharacterEncoding("UTF-8");
        verify(writer).append("foo");
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void testJsonOnlyStatus() throws Exception {
        PrintWriter writer = mock(PrintWriter.class);
        when(servletResponse.getStatus()).thenReturn(0);
        when(servletResponse.getWriter()).thenReturn(writer);

        httpResponse.json(401);

        verify(servletResponse).setStatus(401);
        verify(servletResponse).setContentType("application/json");
        verify(servletResponse).setCharacterEncoding("UTF-8");
        verify(servletResponse, never()).getWriter();
        verify(servletResponse).flushBuffer();
    }

    @Test
    public void foo() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new StreamModule());


        Stream<String> stream = Stream.of("aa", "bb", "cc");

        String json = objectMapper.writeValueAsString(stream);

        assertNotNull(json);

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
