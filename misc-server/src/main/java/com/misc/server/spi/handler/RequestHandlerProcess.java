package com.misc.server.spi.handler;

/**
 * 一个链式的处理器  用于构建{@link RequestHandler}
 */
public class RequestHandlerProcess {

    /**
     * 末节点
     */
    private AbstractRequestHandler last;

    public AbstractRequestHandler getLast() {
        return last;
    }

    /**
     * 头节点 , 优先处理
     */
    private AbstractRequestHandler first;

    public AbstractRequestHandler getFirst() {
        return first;
    }

    public void addFirst(AbstractRequestHandler handler) {
        if (first == null) {
            first = handler;
            last = first;
        } else {
            handler.setNext(first);
            first = handler;
        }
    }

    public void addLast(AbstractRequestHandler handler) {
        if (last == null) {
            last = handler;
            first = last;
        } else {
            last.setNext(handler);
            last = handler;
        }
    }
}
