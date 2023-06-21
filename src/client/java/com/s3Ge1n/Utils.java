package com.s3Ge1n;

import java.util.LinkedList;

public class Utils {
    @SuppressWarnings("SameParameterValue")
    public static <T> void insertToCenter(LinkedList<T> list, T object) {
        int middle = (list.size() + 1) / 2;  // Rounded up
        list.add(middle, object);
    }
}
