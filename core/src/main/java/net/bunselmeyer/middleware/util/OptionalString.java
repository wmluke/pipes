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

@SuppressWarnings("WeakerAccess")
public final class OptionalString implements Serializable {

    private static OptionalString EMPTY = new OptionalString();

    private final String value;

    public static OptionalString empty() {
        return EMPTY;
    }


    public static OptionalString of(CharSequence value) {
        return new OptionalString(value);
    }

    public static OptionalString ofNullable(CharSequence value) {
        return value == null ? EMPTY : new OptionalString(value);
    }

    private OptionalString() {
        this.value = null;
    }

    private OptionalString(CharSequence value) {
        this.value = Objects.requireNonNull(value.toString(), "OptionalString value cannot be null");
    }

    private Optional<String> optional() {
        return Optional.ofNullable(value);
    }

    public OptionalDouble asDouble() {
        return trimToNotPresent()
            .filter(NumberUtils::isNumber)
            .map(Double::parseDouble)
            .map(OptionalDouble::of)
            .orElse(OptionalDouble.empty());
    }

    public OptionalInt asInteger() {
        return trimToNotPresent()
            .filter(NumberUtils::isDigits)
            .map(Integer::parseInt)
            .map(OptionalInt::of)
            .orElse(OptionalInt.empty());
    }

    public OptionalLong asLong() {
        return trimToNotPresent()
            .filter(NumberUtils::isDigits)
            .map(Long::parseLong)
            .map(OptionalLong::of)
            .orElse(OptionalLong.empty());
    }

    /**
     * Returns an Optional<Boolean> that can be empty
     */
    public Optional<Boolean> asBoolean() {
        return trimToNotPresent()
            .map(BooleanUtils::toBooleanObject);
    }

    /**
     * Returns an Optional<Boolean> that is not empty.
     * Null and blank values are false.
     */
    public Optional<Boolean> asBool() {
        /**
         * Would be nice to just do this...
         * ```
         * optional()
         *   .map(StringUtils::trimToNull) 
         *   .map(BooleanUtils::toBoolean)
         * ```  
         * https://developer.atlassian.com/blog/2015/08/optional-broken/  
         */
        return value == null ? Optional.of(false) : optional()
            .map(StringUtils::trimToEmpty)
            .map(BooleanUtils::toBoolean);
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
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

    public OptionalString trimToNotPresent() {
        return OptionalString.ofNullable(StringUtils.trimToNull(value));
    }

    public OptionalString trim() {
        return OptionalString.ofNullable(StringUtils.trimToEmpty(value));
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
