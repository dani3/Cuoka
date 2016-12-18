package com.wallakoala.wallakoala.Utils;

import java.util.HashSet;

/**
 *
 * Created by Daniel Mancebo Aldea on 18/12/2016.
 */

public class ConcurrentSet<E> extends HashSet<E>
{
    private static final Object lock = new Object();

    public ConcurrentSet()
    {
        super();
    }

    @Override
    public boolean remove(Object o)
    {
        synchronized (lock)
        {
            return super.remove(o);
        }
    }

    @Override
    public boolean add(E o)
    {
        synchronized (lock)
        {
            return super.add(o);
        }
    }
}
