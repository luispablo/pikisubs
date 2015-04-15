package com.mediator.model;

/**
 * Created by luispablo on 15/04/15.
 */
public interface CacheFallback<T> {

    T onNotFoundOnCache(String key);
}