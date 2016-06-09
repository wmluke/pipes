package net.bunselmeyer.middleware.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class OptionalStringTest {

    @Test
    public void asDouble() throws Exception {
        assertEquals(12.34, OptionalString.of("12.34").asDouble().getAsDouble(), 0.0001);
        assertEquals(12.34, OptionalString.of("  12.34   ").asDouble().getAsDouble(), 0.0001);
        assertEquals(false, OptionalString.of("xyz").asDouble().isPresent());
        assertEquals(false, OptionalString.of("").asDouble().isPresent());
        assertEquals(false, OptionalString.of("   ").asDouble().isPresent());
        assertEquals(false, OptionalString.ofNullable(null).asDouble().isPresent());
    }

    @Test
    public void asInteger() throws Exception {
        assertEquals(12, OptionalString.of("12").asInteger().getAsInt());
        assertEquals(12, OptionalString.of("  12   ").asInteger().getAsInt());
        assertEquals(false, OptionalString.of("12.34").asInteger().isPresent());
        assertEquals(false, OptionalString.of("xyz").asInteger().isPresent());
        assertEquals(false, OptionalString.of("").asInteger().isPresent());
        assertEquals(false, OptionalString.of("   ").asInteger().isPresent());
        assertEquals(false, OptionalString.ofNullable(null).asInteger().isPresent());
    }

    @Test
    public void asLong() throws Exception {
        assertEquals(12, OptionalString.of("12").asLong().getAsLong());
        assertEquals(12, OptionalString.of("  12   ").asLong().getAsLong());
        assertEquals(false, OptionalString.of("12.34").asLong().isPresent());
        assertEquals(false, OptionalString.of("xyz").asLong().isPresent());
        assertEquals(false, OptionalString.of("").asLong().isPresent());
        assertEquals(false, OptionalString.of("   ").asLong().isPresent());
        assertEquals(false, OptionalString.ofNullable(null).asLong().isPresent());
    }

    @Test
    public void asBoolean() throws Exception {
        assertEquals(false, OptionalString.of("false").asBoolean().get());
        assertEquals(false, OptionalString.of("no").asBoolean().get());
        assertEquals(false, OptionalString.of("off").asBoolean().get());

        assertEquals(true, OptionalString.of("  true   ").asBoolean().get());
        assertEquals(true, OptionalString.of("true").asBoolean().get());
        assertEquals(true, OptionalString.of("yes").asBoolean().get());
        assertEquals(true, OptionalString.of("on").asBoolean().get());

        assertEquals(false, OptionalString.of("xyz").asBoolean().isPresent());
        assertEquals(false, OptionalString.of("").asBoolean().isPresent());
        assertEquals(false, OptionalString.of("   ").asBoolean().isPresent());
        assertEquals(false, OptionalString.ofNullable(null).asBoolean().isPresent());
    }

}