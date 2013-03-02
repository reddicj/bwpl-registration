package org.bwpl.registration.validation

import org.apache.commons.lang.StringUtils

public enum Status {

    INVALID, VALID, DELETED

    private static Map<String, Status> fromStringMap =
        ["INVALID":INVALID, "VALID":VALID, "DELETED":DELETED]

    static Status fromString(String str) {

        if (str == null) return INVALID
        Status s = fromStringMap.get(str.toUpperCase())
        if (s == null) return INVALID
        return s
    }

    String toString() {
        return StringUtils.capitalise(super.toString().toLowerCase())
    }
}