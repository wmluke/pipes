package net.bunselmeyer.evince.http.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.bunselmeyer.util.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class HttpRequestServletAdapterTest {

    private HttpRequestServletAdapter formUrlEncodedHttpRequest;
    private HttpRequestServletAdapter jsonHttpRequest;
    public static final TypeReference<Map<String, String>> TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    @Before
    public void setUp() throws Exception {

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Cookie", "foo=bar; meh=blah");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        headers.put("Host", "localhost:8888");
        headers.put("Accept-Encoding", "gzip,deflate,sdch");
        headers.put("Accept-Language", "en-US,en;q=0.8");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonUtil.configureJsonObjectMapper(objectMapper);

        formUrlEncodedHttpRequest = new HttpRequestServletAdapter(createMockHttpServletRequest(headers, "a=1&b=3&a=2", "aa=11&bb=22"), objectMapper, null);
        jsonHttpRequest = new HttpRequestServletAdapter(createMockHttpServletRequest(headers, "a=1&b=3&a=2", "{\"aa\": 11, \"bb\": 22}"), objectMapper, null);
    }

    @Test
    public void testQueryParams() throws Exception {
        assertThat(formUrlEncodedHttpRequest.query(), is("a=1&b=3&a=2"));
        assertThat(formUrlEncodedHttpRequest.queryParam("a"), is("1"));
        List<String> value = Lists.newArrayList("1", "2");
        assertThat(formUrlEncodedHttpRequest.queryParams("a"), is(value));
        assertThat(formUrlEncodedHttpRequest.queryParam("b"), is("3"));
    }

    @Test
    public void testHeaders() throws Exception {
        assertThat(formUrlEncodedHttpRequest.header("Cookie"), is("foo=bar; meh=blah"));
        assertThat(formUrlEncodedHttpRequest.header("Accept"), is("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        assertThat(formUrlEncodedHttpRequest.header("Connection"), is("keep-alive"));
        assertThat(formUrlEncodedHttpRequest.header("User-Agent"), is("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36"));
    }

    @Test
    public void testCookies() throws Exception {
        assertThat(formUrlEncodedHttpRequest.cookie("foo").getValue(), is("bar"));
        assertThat(formUrlEncodedHttpRequest.cookie("meh").getValue(), is("blah"));
        assertThat(formUrlEncodedHttpRequest.cookies().size(), is(2));
    }

    @Test
    public void testBody() throws Exception {
        assertThat(formUrlEncodedHttpRequest.body().asText(), is("aa=11&bb=22"));
        assertThat(formUrlEncodedHttpRequest.body().asFormUrlEncoded().get("aa"), hasItem("11"));
        assertThat(formUrlEncodedHttpRequest.body().asFormUrlEncoded().get("bb"), hasItem("22"));

        assertThat(jsonHttpRequest.body().asText(), is("{\"aa\": 11, \"bb\": 22}"));
        assertThat(jsonHttpRequest.body().asJson().path("aa").asText(), is("11"));
        assertThat(jsonHttpRequest.body().asJson().path("bb").asText(), is("22"));
        assertThat(jsonHttpRequest.body().asJson(TYPE_REFERENCE).get("aa"), is("11"));
        assertThat(jsonHttpRequest.body().asJson(TYPE_REFERENCE).get("bb"), is("22"));
    }


    private static HttpServletRequest createMockHttpServletRequest(Map<String, String> headers, String queryString, String body) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));

        Map<String, Object> requestAttributes = new HashMap<>();

        Answer setAttributeAnswer = (invocation) -> {
            requestAttributes.put((String) invocation.getArguments()[0], invocation.getArguments()[1]);
            return null;
        };
        doAnswer(setAttributeAnswer).when(servletRequest).setAttribute(anyString(), any());
        when(servletRequest.getAttribute(anyString())).then(invocation -> requestAttributes.get((String) invocation.getArguments()[0]));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            when(servletRequest.getHeader(entry.getKey())).thenReturn(entry.getValue());
        }

        when(servletRequest.getQueryString()).thenReturn(queryString);

        final InputStream inputStream = IOUtils.toInputStream(body, "UTF-8");


        when(servletRequest.getInputStream()).thenReturn(new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return inputStream.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return inputStream.read(b, off, len);
            }
        });
        return servletRequest;
    }
}
