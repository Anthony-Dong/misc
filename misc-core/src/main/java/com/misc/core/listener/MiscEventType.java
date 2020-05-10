package com.misc.core.listener;

/**
 * MiscEvent 的类型
 */
public enum MiscEventType {
    SERVER_START,
    SERVER_SHUTDOWN,
    SERVER_READ,
    SERVER_HANDLER_REMOVED,
    SERVER_CHANNEL_REGISTERED,


    CLIENT_CONNECTED,
    CLIENT_START,
    CLIENT_SHUTDOWN,
    CLIENT_READ,
}
