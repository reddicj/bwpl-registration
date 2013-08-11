package org.bwpl.registration.utils

import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import static org.joda.time.DateTimeConstants.SUNDAY
import static org.joda.time.DateTimeConstants.WEDNESDAY

class BwplDateTime {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm")
    private static final DateTimeFormatter fileNameDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    private static final DateTimeFormatter asaDateOfBirthFormat = DateTimeFormat.forPattern("d MMMM yyyy")

    private final DateTime currentDateTime

    private BwplDateTime(DateTime dateTime) {
        this.currentDateTime = dateTime
    }

    static BwplDateTime getNow() {
        return fromJodaDate(new DateTime())
    }

    static BwplDateTime fromJodaDate(DateTime dateTime) {
        return new BwplDateTime(dateTime)
    }

    static BwplDateTime fromJavaDate(Date date) {
        return fromJodaDate(new DateTime(date))
    }

    static BwplDateTime fromString(String str) {
        return fromJodaDate(dateFormatter.parseDateTime(str))
    }

    static BwplDateTime fromString(String str, String format) {

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(format)
        return fromJodaDate(dateFormatter.parseDateTime(str))
    }

    static BwplDateTime fromASADateOfBirthString(String str) {

        if (StringUtils.isEmpty(str)) return null
        def m = str =~ /^([0-9]{1,2})[a-z]{2} ([A-Za-z]+) ([0-9]{4})$/
        String s = "${m[0][1]} ${m[0][2]} ${m[0][3]}"
        return fromJodaDate(asaDateOfBirthFormat.parseDateTime(s))
    }

    BwplDateTime getWedMidnight() {

        DateTime wed = currentDateTime.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        return fromJodaDate(wed)
    }

    boolean isDiffLessThan18Years(BwplDateTime endDate) {

        Period p = new Period(currentDateTime, endDate.toJodaDateTime())
        int years = p.getYears()
        return years < 18
    }

    boolean isBefore(BwplDateTime compareToDate) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(compareToDate.toJodaDateTime(), currentDateTime) > 0
    }

    boolean isAfter(BwplDateTime compareToDate) {

        DateTimeComparator comparator = DateTimeComparator.dateOnlyInstance
        return comparator.compare(currentDateTime, compareToDate.toJodaDateTime()) > 0
    }

    boolean isSunEve() {
        return (currentDateTime.dayOfWeek == SUNDAY) && (currentDateTime.hourOfDay >= 20)
    }

    boolean isDuringValidationCutOff(BwplDateTime seasonStartDate, BwplDateTime dateTime) {
        return dateTime.isAfter(seasonStartDate) && dateTime.isAfter(wedMidnight) && (!isSunEve())
    }

    String toDateString() {

        if (currentDateTime == null) return ""
        DateTime dt = new DateTime(currentDateTime)
        return dateFormatter.print(dt)
    }

    String toDateTimeString() {

        if (currentDateTime == null) return ""
        DateTime dt = new DateTime(currentDateTime)
        return dateTimeFormatter.print(dt)
    }

    String toString() {
        return toDateTimeString()
    }

    String toFileNameDateTimeString() {

        if (currentDateTime == null) return ""
        DateTime dt = new DateTime(currentDateTime)
        return fileNameDateTimeFormatter.print(dt)
    }

    DateTime toJodaDateTime() {
        return currentDateTime
    }

    Date toJavaDate() {
        return currentDateTime.toDate()
    }
}
