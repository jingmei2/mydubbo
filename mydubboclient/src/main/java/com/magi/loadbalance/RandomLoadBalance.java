package com.magi.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    /**
     * 负载均衡算法,在 url 数组里选择最优的一个
     * list----one
     *
     * @param repos
     * @return
     */
    @Override
    public String select(List<String> repos) {
        int len = repos.size();
        Random random = new Random();
        return len!=0?repos.get(random.nextInt(len)):"error";
    }
}
