package com.star.svn.function;

import com.google.common.collect.Lists;

import java.util.List;

public final class Streams {
    
    public static <T> List<T> filter(List<T> source, Predicate predicate){
        List<T> result = Lists.newArrayList();
        for (T t : source) {
            if (predicate.is(t)){
                result.add(t);
            }
        }
        return result;
    }
}
