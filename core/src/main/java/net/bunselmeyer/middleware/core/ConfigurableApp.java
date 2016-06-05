package net.bunselmeyer.middleware.core;


import java.util.function.Consumer;

public interface ConfigurableApp<A extends ConfigurableApp> {

    <C> A configure(Class<C> type, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> A configure(Class<C> type, String name, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> A configure(C configuration, Consumer<C> consumer);

    <C> A configure(C configuration, String name, Consumer<C> consumer);

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

}
