package org.bwpl.registration.utils

import org.apache.commons.lang.StringUtils

class StringMunger {

    static String munge(String str) {

        if (StringUtils.isEmpty(str)) return str
        StringBuilder sb = new StringBuilder()
        str.toCharArray().each { c ->
            if (c.isLetterOrDigit()) {
                sb << c
            }
            else if (c.isWhitespace()) {
                sb << c
            }
        }
        return sb.toString()
    }
}
