package net.bunselmeyer.middleware.core;


import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigurableApp<A extends ConfigurableApp> {

    <C> A configure(Class<C> type, Consumer<C> consumer);

    <C> A configure(Class<C> type, String name, Consumer<C> consumer);

    <C, S extends C> A configure(Class<C> type, Supplier<S> supplier, Consumer<S> consumer);

    <C, S extends C> A configure(Class<C> type, Supplier<S> supplier, String name, Consumer<S> consumer);

    <C> A configure(C configuration, Consumer<C> consumer);

    <C> A configure(C configuration, String name, Consumer<C> consumer);

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

}
