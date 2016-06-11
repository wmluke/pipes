package net.bunselmeyer.middleware.util;

import org.junit.Test;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OptionalsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void ifPresent() throws Exception {

        BiConsumer<Integer, String> consumer1 = mock(BiConsumer.class);

        Optionals.ifPresent(Optional.of(4), Optional.of("abc"), consumer1);
        Optionals.ifPresent(Optional.<Integer>empty(), Optional.of("abc"), consumer1);
        Optionals.ifPresent(Optional.of(4), Optional.<String>empty(), consumer1);

        verify(consumer1, times(1)).accept(4, "abc");
        verify(consumer1, times(1)).accept(anyInt(), anyString());
    }

    @Test
    public void map() throws Exception {

        assertEquals(7, Optionals
            .map(Optional.of(4), Optional.of("abc"), (a, b) -> a + b.trim().length())
            .orElseThrow(RuntimeException::new)
            .intValue());

        assertEquals(false, Optionals
            .map(Optional.<Integer>empty(), Optional.of("abc"), (a, b) -> a + b.trim().length())
            .isPresent());

        assertEquals(false, Optionals
            .map(Optional.of(4), Optional.<String>empty(), (a, b) -> a + b.trim().length())
            .isPresent());

        assertEquals(false, Optionals
            .map(Optional.of(4), Optional.of("abc"), (a, b) -> null)
            .isPresent());

    }

}