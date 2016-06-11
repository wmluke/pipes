package net.bunselmeyer.middleware.util;


import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
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

    public Optional<String> asOptional() {
        return Optional.ofNullable(value);
    }

    public Optional<Double> asDouble() {
        return trimToNotPresent()
            .filter(NumberUtils::isNumber)
            .map(Double::parseDouble);
    }

    public Optional<Integer> asInteger() {
        return trimToNotPresent()
            .filter(NumberUtils::isDigits)
            .map(Integer::parseInt);
    }

    public Optional<Long> asLong() {
        return trimToNotPresent()
            .filter(NumberUtils::isDigits)
            .map(Long::parseLong);
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
        return value == null ? Optional.of(false) : asOptional()
            .map(StringUtils::trimToEmpty)
            .map(BooleanUtils::toBoolean);
    }

    public <U> Optional<U> map(Function<? super String, ? extends U> mapper) {
        return asOptional().map(mapper);
    }


    public String orElse(String other) {
        return asOptional().orElse(other);
    }

    public <X extends Throwable> String orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return asOptional().orElseThrow(exceptionSupplier);
    }

    public OptionalString filter(Predicate<? super String> predicate) {
        return asOptional()
            .filter(predicate)
            .map(OptionalString::of)
            .orElse(OptionalString.empty());
    }

    public void ifPresent(Consumer<? super String> consumer) {
        asOptional().ifPresent(consumer);
    }

    public <U> Optional<U> flatMap(Function<? super String, Optional<U>> mapper) {
        return asOptional().flatMap(mapper);
    }

    public String orElseGet(Supplier<? extends String> other) {
        return asOptional().orElseGet(other);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String get() {
        return asOptional().get();
    }

    public boolean isPresent() {
        return asOptional().isPresent();
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
