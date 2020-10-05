package org.esupportail.smsu.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static <T> List<T> concat(List<T> l1, List<T> l2) {
        return Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList());
    }
}