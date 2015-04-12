package com.mediator.tasks;

/**
 * Created by luispablo on 12/04/15.
 */
public interface TaskDoneListener<T> {

    public void onDone(T t);
}