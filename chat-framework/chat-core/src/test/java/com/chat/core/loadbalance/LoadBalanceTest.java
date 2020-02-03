package com.chat.core.loadbalance;

import com.chat.core.util.Pair;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.*;

public class LoadBalanceTest {


    public LoadBalance loadBalance() {
        return set -> {
            Optional<Pair<InetSocketAddress, Integer>> first = set.stream().min(Comparator.comparingInt(Pair::getV));
            return first.map(Pair::getK);
        };
    }

    @Test
    public void test() {
        LoadBalance loadBalance = loadBalance();
        HashSet<Pair<InetSocketAddress, Integer>> set = new HashSet<>();

        set.add(new Pair<>(new InetSocketAddress(111), 1));
        set.add(new Pair<>(new InetSocketAddress(222), 2));

        Optional<InetSocketAddress> optional = loadBalance.loadBalance(set);

        optional.ifPresent(System.out::println);
    }
}