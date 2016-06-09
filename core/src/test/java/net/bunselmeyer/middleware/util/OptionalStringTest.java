package net.bunselmeyer.middleware.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class OptionalStringTest {


    @Test
    public void equalTo() throws Exception {
        assertEquals(true, OptionalString.of("foo").equalTo("foo"));
        assertEquals(true, OptionalString.of("foo").equalToIgnoreCase("FOO"));
        assertEquals(true, OptionalString.of("  foo  ").trimToNotPresent().equalTo("foo"));

        assertEquals(false, OptionalString.of(" foo ").equalTo("foo"));
        assertEquals(false, OptionalString.of("foo").equalTo("bar"));
    }

    @Test
    public void equals() throws Exception {
        assertEquals(true, OptionalString.of("foo").equals(OptionalString.of("foo")));
        assertEquals(true, OptionalString.ofNullable(null).equals(OptionalString.empty()));
        assertEquals(false, OptionalString.of("foo").equals("foo"));
        assertEquals(false, OptionalString.empty().equals(null));
    }

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

    @Test
    public void filter() throws Exception {
        assertEquals(true, OptionalString.of("abc").filter(StringUtils::isNotBlank).equalTo("abc"));
        assertEquals(false, OptionalString.of("    ").filter(StringUtils::isNotBlank).isPresent());
    }

    @Test
    public void isPresent() throws Exception {
        assertEquals(true, OptionalString.of(" abc  ").isPresent());
        assertEquals(true, OptionalString.of("   ").isPresent());
        assertEquals(false, OptionalString.ofNullable(null).isPresent());
        assertEquals(false, OptionalString.of("   ").trimToNotPresent().isPresent());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("OptionalString[foo]", OptionalString.of("foo").toString());
        assertEquals("OptionalString.empty", OptionalString.empty().toString());
    }
}