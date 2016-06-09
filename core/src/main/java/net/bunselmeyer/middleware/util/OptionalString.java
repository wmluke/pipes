package net.bunselmeyer.middleware.util;


import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class OptionalString implements Serializable {

    private static OptionalString EMPTY = new OptionalString();

    private final String value;

    public static OptionalString empty() {
        return EMPTY;
    }

    public static OptionalString of(String value) {
        return new OptionalString(value);
    }

    public static OptionalString ofNullable(String value) {
        return value == null ? EMPTY : new OptionalString(value);
    }

    private OptionalString() {
        this.value = null;
    }

    private OptionalString(String value) {
        this.value = Objects.requireNonNull(value, "OptionalString value cannot be null");
    }

    private Optional<String> optional() {
        return Optional.ofNullable(value);
    }

    public OptionalDouble asDouble() {
        return trim()
            .filter(NumberUtils::isNumber)
            .map(Double::parseDouble)
            .map(OptionalDouble::of)
            .orElse(OptionalDouble.empty());
    }

    public OptionalInt asInteger() {
        return trim()
            .filter(NumberUtils::isDigits)
            .map(Integer::parseInt)
            .map(OptionalInt::of)
            .orElse(OptionalInt.empty());
    }

    public OptionalLong asLong() {
        return trim()
            .filter(NumberUtils::isDigits)
            .map(Long::parseLong)
            .map(OptionalLong::of)
            .orElse(OptionalLong.empty());
    }

    public Optional<Boolean> asBoolean() {
        return trim()
            .map(BooleanUtils::toBooleanObject);
    }

    public <U> Optional<U> map(Function<? super String, ? extends U> mapper) {
        return optional().map(mapper);
    }

    public String orElse(String other) {
        return optional().orElse(other);
    }

    public <X extends Throwable> String orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return optional().orElseThrow(exceptionSupplier);
    }

    public OptionalString filter(Predicate<? super String> predicate) {
        return optional()
            .filter(predicate)
            .map(OptionalString::of)
            .orElse(OptionalString.empty());
    }

    public void ifPresent(Consumer<? super String> consumer) {
        optional().ifPresent(consumer);
    }

    public <U> Optional<U> flatMap(Function<? super String, Optional<U>> mapper) {
        return optional().flatMap(mapper);
    }

    public String orElseGet(Supplier<? extends String> other) {
        return optional().orElseGet(other);
    }

    public String get() {
        return optional().get();
    }

    public boolean isPresent() {
        return optional().isPresent();
    }

    public boolean equalTo(String other) {
        return Objects.equals(value, other);
    }

    public boolean equalToIgnoreCase(String other) {
        return StringUtils.equalsIgnoreCase(value, other);
    }

    public OptionalString trim() {
        return OptionalString.ofNullable(StringUtils.trimToNull(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OptionalString)) {
            return false;
        }

        OptionalString other = (OptionalString) obj;
        return Objects.equals(value, other.value);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
            ? String.format("OptionalString[%s]", value)
            : "OptionalString.empty";
    }
}
