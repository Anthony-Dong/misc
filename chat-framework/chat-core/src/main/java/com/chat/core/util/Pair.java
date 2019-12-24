package com.chat.core.util;

import java.util.Objects;

/**
 * Pair,处理尴尬
 *
 * @date:2019/12/24 22:03
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */

public class Pair<K, V> {

    private K k;
    private V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(getK(), pair.getK()) &&
                Objects.equals(getV(), pair.getV());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getK(), getV());
    }

    @Override
    public String toString() {
        return "Pair{" +
                "k=" + k +
                ", v=" + v +
                '}';
    }
}
