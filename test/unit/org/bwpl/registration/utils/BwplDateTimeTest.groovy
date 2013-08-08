package org.bwpl.registration.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.joda.time.DateTimeConstants.WEDNESDAY

class BwplDateTimeTest {

    @Test
    void testFromString() {

        BwplDateTime bwplDateTime = BwplDateTime.fromString("02-08-2013 23:59", "dd-MM-yyyy HH:mm")
        assertThat(bwplDateTime.toDateString()).isEqualTo("02-08-2013")
    }

    @Test
    void testIsBeforeIsAfter() {

        BwplDateTime d1 = BwplDateTime.fromJodaDate(new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12))
        BwplDateTime d2 = BwplDateTime.fromJodaDate(new DateTime().withYear(2011).withMonthOfYear(5).withDayOfMonth(12))
        BwplDateTime d3 = BwplDateTime.fromJodaDate(new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12))

        assertThat(d1.isBefore(d2)).isFalse()
        assertThat(d1.isAfter(d2)).isTrue()
        assertThat(d1.isBefore(d3)).isFalse()
        assertThat(d1.isAfter(d3)).isFalse()
    }

    @Test
    void testToDateString() {

        BwplDateTime dt = BwplDateTime.fromJodaDate(new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12))
        assertThat(dt.toDateString()).isEqualTo("12-05-2012")
    }

    @Test
    void testASADateOfBirthParse() {

        String asaDateOfBirth = "8st October 1992"
        BwplDateTime bwplDateTime = BwplDateTime.fromASADateOfBirthString(asaDateOfBirth)
        DateTime dt = bwplDateTime.toJodaDateTime()
        assertThat(dt.dayOfMonth().get()).isEqualTo(8)
        assertThat(dt.monthOfYear().get()).isEqualTo(10)
        assertThat(dt.year().get()).isEqualTo(1992)
    }

    @Test
    void testASADateOfBirthParseForEmptyString() {

        String asaDateOfBirth = ""
        BwplDateTime bwplDateTime = BwplDateTime.fromASADateOfBirthString(asaDateOfBirth)
        assertThat(bwplDateTime).isNull()
    }

    @Test
    void testIsPeriodLessThan18Years() {

        BwplDateTime startDate = BwplDateTime.fromJodaDate(new DateTime().withYear(1972).withMonthOfYear(4).withDayOfMonth(5))
        BwplDateTime endDate = BwplDateTime.fromJodaDate(new DateTime().withYear(1990).withMonthOfYear(4).withDayOfMonth(4))
        assertThat(startDate.isDiffLessThan18Years(endDate)).isTrue()
        endDate = BwplDateTime.fromJodaDate(new DateTime().withYear(1990).withMonthOfYear(4).withDayOfMonth(5))
        assertThat(startDate.isDiffLessThan18Years(endDate)).isFalse()
    }

    @Test
    void testWedMidnight() {

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
        DateTime wed = null

        DateTime sat = BwplDateTime.fromString("03-08-2013").toJodaDateTime()
        wed = sat.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("31-07-2013")

        DateTime sun = BwplDateTime.fromString("04-08-2013").toJodaDateTime()
        wed = sun.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("31-07-2013")

        DateTime mon = BwplDateTime.fromString("05-08-2013").toJodaDateTime()
        wed = mon.withDayOfWeek(WEDNESDAY).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59)
        assertThat(dateFormatter.print(wed)).isEqualTo("07-08-2013")
    }

    @Test
    void testGetPrevWedMidnight() {

        BwplDateTime prevWed = BwplDateTime.now.wedMidnight
        assertThat(prevWed.toJodaDateTime().dayOfWeek).isEqualTo(WEDNESDAY)
        assertThat(prevWed.toJodaDateTime().hourOfDay).isEqualTo(23)
        assertThat(prevWed.toJodaDateTime().minuteOfHour).isEqualTo(59)
        assertThat(prevWed.toJodaDateTime().secondOfMinute).isEqualTo(59)
    }
}
