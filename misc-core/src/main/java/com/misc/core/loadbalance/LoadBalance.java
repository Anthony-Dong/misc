package com.misc.core.loadbalance;


import java.util.List;

/**
 * 简单实现
 */
public interface LoadBalance<T> {

    T loadBalance(List<T> set);
}
