package com.shop.image.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ShardRouter {
    public int resolveShard(UUID id) {
        char c = id.toString().charAt(0);
        String s = String.valueOf(c);
        if("0123".contains(s)) return 0;
        if("4567".contains(s)) return 1;
        if("89ab".contains(s)) return 2;
        if("cdef".contains(s)) return 3;
        else return -1;
    }
}