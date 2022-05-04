package com.goldmedal.hrapp.common.customviews.treeview;

/**
 *
 */

final class Conditions {
    private Conditions() {
    }

    static <T> T isNonNull(T object, String message) {
        if(object == null) {
            throw new IllegalArgumentException(message);
        }

        return object;
    }
}