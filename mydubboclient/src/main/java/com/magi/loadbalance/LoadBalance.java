package com.magi.loadbalance;

import java.util.List;

/**
 * @author magi
 */
public interface LoadBalance {
    /**
     * 负载均衡算法,在 url 数组里选择最优的一个
     * list----one
     * @param repos
     * @return
     */
    String select(List<String> repos);
}
