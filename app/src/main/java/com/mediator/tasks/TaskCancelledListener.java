package com.mediator.tasks;

/**
 * Created by luispablo on 12/04/15.
 */
public interface TaskCancelledListener<T> {

    public void onCancelled(T t);
}