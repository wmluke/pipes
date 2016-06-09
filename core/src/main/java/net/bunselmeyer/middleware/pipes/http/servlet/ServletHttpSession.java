package net.bunselmeyer.middleware.pipes.http.servlet;

import net.bunselmeyer.middleware.pipes.http.HttpSession;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

public class ServletHttpSession implements HttpSession {

    private final HttpServletRequest request;

    public ServletHttpSession(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Serializable id() {
        return getServletSession().getId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(String name) {
        return Optional.ofNullable((T) getServletSession().getAttribute(name));
    }

    @Override
    public HttpSession put(String name, Object value) {
        getServletSession().setAttribute(name, value);
        return this;
    }

    @Override
    public <T> Optional<T> remove(String name) {
        Optional<T> optional = get(name);
        optional.ifPresent((value) -> {
            getServletSession().removeAttribute(name);
        });
        return optional;
    }

    @Override
    public Set<String> keys() {
        Enumeration<String> attributeNames = getServletSession().getAttributeNames();
        return new HashSet<>(Collections.list(attributeNames));
    }

    @Override
    public long creationTime() {
        return getServletSession().getCreationTime();
    }

    @Override
    public long lastAccessedTime() {
        return getServletSession().getLastAccessedTime();
    }

    @Override
    public HttpSession invalidate() {
        getServletSession().invalidate();
        return this;
    }

    @Override
    public boolean isNew() {
        return getServletSession().isNew();
    }

    private javax.servlet.http.HttpSession getServletSession() {
        return request.getSession();
    }
}
