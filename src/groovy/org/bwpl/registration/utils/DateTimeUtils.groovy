package org.bwpl.registration.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

class DateTimeUtils {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")
    private static final DateTimeFormatter fileNameDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    private final DateTime d

    static DateTime getRegistrationDeadline() {
        DateTime dt = new DateTime()
        return dt.withYear(2013).withMonthOfYear(7).withDayOfMonth(31)
    }

    static DateTime getSeasonStartDate() {
        DateTime dt = new DateTime()
        return dt.withYear(2013).withMonthOfYear(9).withDayOfMonth(1)
    }

    static boolean isBeforeSeasonStart() {
        DateTimeUtils currentDate = new DateTimeUtils(new DateTime())
        return currentDate.isBefore(DateTimeUtils.seasonStartDate)
    }

    static String printDateTime(DateTime d) {
        return dateFormatter.print(d)
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

    static DateTimeUtils createFromDate(Date d) {
        return new DateTimeUtils(new DateTime(d))
    }

    DateTimeUtils(DateTime d) {
        this.d = d
    }

    boolean isBefore(DateTime d) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(d, this.d) > 0
    }

    boolean isAfter(DateTime d) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(this.d, d) > 0
    }

    String print() {
        print(d)
    }
}
