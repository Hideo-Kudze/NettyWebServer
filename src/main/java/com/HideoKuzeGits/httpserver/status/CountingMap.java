package com.HideoKuzeGits.httpserver.status;



import java.util.HashMap;

public class CountingMap<E> extends HashMap<E, Integer>{

    public void add(E element) {

        Integer count;
        if (containsKey(element))
            count = get(element) + 1;
        else
            count = 1;

        put(element, count);
    }
}
