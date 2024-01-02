package coffee.client.helper.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LimitedSizeList<T extends Comparable<T>> implements Iterable<T> {
    public final int maxSize;
    public final ArrayList<T> elements;

    public LimitedSizeList(int maxSize) {
        this.maxSize = maxSize;
        this.elements = new ArrayList<>(maxSize);
    }


    /**
     * Adds the specified element to the list if there is available space.
     *
     * @param element The element to be added to the list.
     * @return {@code true} if the element was successfully added,
     *         {@code false} if the list has reached its maximum capacity and the element cannot be added.
     */
    public boolean add(T element) {
        if (elements.size() < maxSize) {
            elements.add(element);
            return true;
        }
        return false;
    }

    /**
     * Removes the first occurrence of the specified element from the list, if it is present.
     * If the list does not contain the element, it is unchanged.
     *
     * @param element The element to be removed from the list.
     * @return {@code true} if the list contained the specified element and it was successfully removed,
     *         {@code false} otherwise.
     */
    public boolean remove(T element) {
        return elements.remove(element);
    }

    public void sort() {
        Collections.sort(elements);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return elements.iterator();
    }
}

