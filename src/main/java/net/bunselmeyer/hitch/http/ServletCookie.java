package net.bunselmeyer.hitch.http;

import io.netty.handler.codec.http.Cookie;

import java.util.Set;

public class ServletCookie implements Cookie {

    private final javax.servlet.http.Cookie cookie;
    private boolean discard;

    public ServletCookie(javax.servlet.http.Cookie cookie) {
        this.cookie = cookie;
    }


    @Override
    public String getName() {
        return cookie.getName();
    }

    @Override
    public String getValue() {
        return cookie.getValue();
    }

    @Override
    public void setValue(String value) {
        cookie.setValue(value);
    }

    @Override
    public String getDomain() {
        return cookie.getDomain();
    }

    @Override
    public void setDomain(String domain) {
        cookie.setDomain(domain);
    }

    @Override
    public String getPath() {
        return cookie.getPath();
    }

    @Override
    public void setPath(String path) {
        cookie.setPath(path);
    }

    @Override
    public String getComment() {
        return cookie.getComment();
    }

    @Override
    public void setComment(String comment) {
        cookie.setComment(comment);
    }

    @Override
    public long getMaxAge() {
        return cookie.getMaxAge();
    }

    @Override
    public void setMaxAge(long maxAge) {
        cookie.setMaxAge(new Long(maxAge).intValue());
    }

    @Override
    public int getVersion() {
        return cookie.getVersion();
    }

    @Override
    public void setVersion(int version) {
        cookie.setVersion(version);
    }

    @Override
    public boolean isSecure() {
        return cookie.getSecure();
    }

    @Override
    public void setSecure(boolean secure) {
        cookie.setSecure(secure);
    }

    @Override
    public boolean isHttpOnly() {
        return cookie.isHttpOnly();
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        cookie.setHttpOnly(httpOnly);
    }

    @Override
    public String getCommentUrl() {
        return null;
    }

    @Override
    public void setCommentUrl(String commentUrl) {
        // NOOP
    }

    @Override
    public boolean isDiscard() {
        return discard;
    }

    @Override
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    @Override
    public Set<Integer> getPorts() {
        return null;
    }

    @Override
    public void setPorts(int... ports) {
        // NOOP
    }

    @Override
    public void setPorts(Iterable<Integer> ports) {
        // NOOP
    }

    @Override
    public int compareTo(Cookie o) {
        return o.compareTo(this) * -1;
    }
}
