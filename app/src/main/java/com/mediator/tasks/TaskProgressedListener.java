package com.mediator.tasks;

/**
 * Created by luispablo on 12/04/15.
 */
public interface TaskProgressedListener<T> {

    public void onProgressed(T t);
}