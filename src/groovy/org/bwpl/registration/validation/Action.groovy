package org.bwpl.registration.validation

import org.apache.commons.lang.StringUtils

public enum Action {

    ADDED, DELETED, UNDELETED, VALIDATED

    private static Map<String, Action> fromStringMap =
        ["ADDED":ADDED, "DELETED":DELETED, "UNDELETED":UNDELETED, "VALIDATED":VALIDATED]

    static Action fromString(String str) {

        if (str == null) throw new IllegalArgumentException("Action string value is null")
        Action s = fromStringMap.get(str.toUpperCase())
        if (s == null) throw new IllegalStateException("Invalid action value: $s")
        return s
    }

    String toString() {
        return StringUtils.capitalise(super.toString().toLowerCase())
    }
}