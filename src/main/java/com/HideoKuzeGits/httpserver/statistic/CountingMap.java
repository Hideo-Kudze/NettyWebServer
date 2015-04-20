package com.HideoKuzeGits.httpserver.statistic;


import java.util.HashMap;

/**
 * A order-independent collection that contains no duplicate elements.
 * Maps elements to number of times it was added.
 *
 * @param <E> the type of object that will be counted.
 *
 * @see java.util.HashMap
 */
public class CountingMap<E> extends HashMap<E, Integer> {

    public void add(E element) {

        Integer count;
        if (containsKey(element))
            count = get(element) + 1;
        else
            count = 1;

        put(element, count);
    }

}
