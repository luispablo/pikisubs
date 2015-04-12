package com.mediator.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by luispablo on 11/04/15.
 */
@Root(strict = false)
public class Channel {

    @ElementList(inline = true, required = false, empty = false)
    private List<Subtitle> items;

    public List<Subtitle> getItems() {
        return items;
    }

    public void setItems(List<Subtitle> items) {
        this.items = items;
    }
}