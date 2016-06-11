package net.bunselmeyer.middleware.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Optionals {


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U> void ifPresent(Optional<T> opt1, Optional<U> opt2, BiConsumer<T, U> consumer) {
        opt1.ifPresent((t) -> {
            opt2.ifPresent((u) -> {
                consumer.accept(t, u);
            });
        });

    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, R> Optional<R> map(Optional<T> opt1, Optional<U> opt2, BiFunction<T, U, R> consumer) {
        return opt1
            .map((t) ->
                opt2.map((u) ->
                    consumer.apply(t, u)))
            .orElseGet(Optional::empty);

    }

}
