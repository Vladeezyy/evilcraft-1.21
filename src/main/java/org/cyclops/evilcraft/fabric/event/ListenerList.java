package org.cyclops.evilcraft.fabric.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Listeners of one of the mod's events (the events of NeoForge that Fabric API does not have).
 */
public class ListenerList<E> {

    private final List<Consumer<E>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<E> listener) {
        listeners.add(listener);
    }

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    public E post(E event) {
        for (Consumer<E> listener : listeners) {
            listener.accept(event);
        }
        return event;
    }
}
