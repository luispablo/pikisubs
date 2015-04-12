package com.mediator.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by luispablo on 11/04/15.
 */
@Root(strict = false)
public class Rss {
    @Element
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}