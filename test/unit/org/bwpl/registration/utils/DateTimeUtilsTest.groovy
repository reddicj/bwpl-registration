package org.bwpl.registration.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.joda.time.DateTimeConstants.*

class DateTimeUtilsTest {

    @Test
    void testIsBeforeIsAfter() {

        DateTime d1 = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)
        DateTime d2 = new DateTime().withYear(2011).withMonthOfYear(5).withDayOfMonth(12)
        DateTime d3 = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)

        assertThat(DateTimeUtils.isBefore(d1, d2)).isFalse()
        assertThat(DateTimeUtils.isAfter(d1, d2)).isTrue()
        assertThat(DateTimeUtils.isBefore(d1, d3)).isFalse()
        assertThat(DateTimeUtils.isAfter(d1, d3)).isFalse()
    }

    @Test
    void testPrint() {

        DateTime dt = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)
        Date d = dt.toDate()
        String str = DateTimeUtils.printDate(d)
        assertThat(str).isEqualTo("12-05-2012")
    }

    @Test
    void testASADateOfBirthParse() {

        String asaDateOfBirth = "8st October 1992"
        DateTime dt = DateTimeUtils.parseASADateOfBirth(asaDateOfBirth)
        assertThat(dt.dayOfMonth().get()).isEqualTo(8)
        assertThat(dt.monthOfYear().get()).isEqualTo(10)
        assertThat(dt.year().get()).isEqualTo(1992)
    }

    @Test
    void testASADateOfBirthParseForEmptyString() {

        String asaDateOfBirth = ""
        DateTime dt = DateTimeUtils.parseASADateOfBirth(asaDateOfBirth)
        assertThat(dt).isNull()
    }

    @Test
    void testIsPeriodLessThan18Years() {

        DateTime startDate = new DateTime().withYear(1972).withMonthOfYear(4).withDayOfMonth(5)
        DateTime endDate = new DateTime().withYear(1990).withMonthOfYear(4).withDayOfMonth(4)
        assertThat(DateTimeUtils.isPeriodLessThan18Years(startDate, endDate)).isTrue()
        endDate = new DateTime().withYear(1990).withMonthOfYear(4).withDayOfMonth(5)
        assertThat(DateTimeUtils.isPeriodLessThan18Years(startDate, endDate)).isFalse()
    }

    @Test
    void testWedMidnight() {

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
        DateTime wed = null

        DateTime sat = DateTimeUtils.parse("03-08-2013")
        wed = sat.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("31-07-2013")

        DateTime sun = DateTimeUtils.parse("04-08-2013")
        wed = sun.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("31-07-2013")

        DateTime mon = DateTimeUtils.parse("05-08-2013")
        wed = mon.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("07-08-2013")
    }

    @Test
    void testGetPrevWedMidnight() {

        DateTime prevWed = new DateTimeUtils().wedMidnight
        assertThat(prevWed.dayOfWeek).isEqualTo(WEDNESDAY)
        assertThat(prevWed.hourOfDay).isEqualTo(23)
        assertThat(prevWed.minuteOfHour).isEqualTo(59)
        assertThat(prevWed.secondOfMinute).isEqualTo(59)
    }

    @Test
    void testIsBeforeWedMidnight() {

        DateTime mon = new DateTime().withDayOfWeek(MONDAY).withHourOfDay(0).withMinuteOfHour(1)
        DateTime sun = new DateTime().withDayOfWeek(SUNDAY).withHourOfDay(23).withMinuteOfHour(59)
        assertThat(new DateTimeUtils().isBeforeWedMidnight(mon)).isTrue()
        assertThat(new DateTimeUtils().isBeforeWedMidnight(sun)).isFalse()
    }
}
