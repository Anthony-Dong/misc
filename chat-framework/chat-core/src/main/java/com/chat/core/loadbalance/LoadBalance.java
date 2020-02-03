package com.chat.core.loadbalance;

import com.chat.core.util.Pair;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Optional;

/**
 * TODO
 *
 * @date:2020/1/21 17:17
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public interface LoadBalance {


    Optional<InetSocketAddress> loadBalance(HashSet<Pair<InetSocketAddress, Integer>> set);

}
