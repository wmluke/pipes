package net.bunselmeyer.middleware.pipes.http;


import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface HttpSession extends Serializable {

    Serializable id();

    long creationTime();

    long lastAccessedTime();

    <T> Optional<T> get(String name);

    HttpSession put(String name, Object value);

    <T> Optional<T> remove(String name);

    Set<String> keys();

    HttpSession invalidate();

    boolean isNew();

    default HttpSession putIfAbsent(String name, Object value) {
        if (!get(name).isPresent()) put(name, value);
        return this;
    }

    default Collection<Object> values() {
        return keys().stream()
            .map((key) -> this.get(key).orElse(null))
            .collect(Collectors.toList());
    }

    default Set<Pair<String, Object>> entries() {
        return keys().stream()
            .map((key) -> Pair.of(key, this.get(key).orElse(null)))
            .collect(Collectors.toSet());
    }


}
