package com.chat.core.loadbalance;

import com.chat.core.util.Pair;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;


public interface LoadBalance {

    Optional<InetSocketAddress> loadBalance(Set<Pair<InetSocketAddress, Integer>> set);

}
