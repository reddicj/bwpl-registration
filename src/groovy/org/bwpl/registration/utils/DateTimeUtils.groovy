package org.bwpl.registration.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.apache.commons.lang.StringUtils

class DateTimeUtils {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")
    private static final DateTimeFormatter fileNameDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    private static final DateTimeFormatter asaDateOfBirthFormat = DateTimeFormat.forPattern("d MMMM yyyy")

    GrailsApplication grailsApplication

    DateTime getPreSeasonDeadline() {

        String s = grailsApplication.config.bwpl.registration.pre.season.date
        return parse(s)
    }

    DateTime getSeasonStartDate() {

        String s = grailsApplication.config.bwpl.registration.season.start.date
        return parse(s)
    }

    boolean isBeforeSeasonStart() {
        return isBefore(new DateTime(), seasonStartDate)
    }

    static String printDate(Date d) {

        if (d == null) return ""
        DateTime dt = new DateTime(d)
        return dateFormatter.print(dt)
    }

    static String printDateTime(Date d) {

        if (d == null) return ""
        DateTime dt = new DateTime(d)
        return dateTimeFormatter.print(dt)
    }

    static String printFileNameDateTime(Date d) {

        if (d == null) return ""
        DateTime dt = new DateTime(d)
        return fileNameDateTimeFormatter.print(dt)
    }

    static DateTime parse(String str) {
        return dateFormatter.parseDateTime(str)
    }

    static DateTime parse(String str, String format) {

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(format)
        return dateFormatter.parseDateTime(str)
    }

    static DateTime parseASADateOfBirth(String str) {

        if (StringUtils.isEmpty(str)) return null
        def m = str =~ /^([0-9]{1,2})[a-z]{2} ([A-Za-z]+) ([0-9]{4})$/
        String s = "${m[0][1]} ${m[0][2]} ${m[0][3]}"
        return asaDateOfBirthFormat.parseDateTime(s)
    }

    static boolean isBefore(DateTime date, DateTime compareToDate) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(compareToDate, date) > 0
    }

    static boolean isAfter(DateTime date, DateTime compareToDate) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(date, compareToDate) > 0
    }
}
