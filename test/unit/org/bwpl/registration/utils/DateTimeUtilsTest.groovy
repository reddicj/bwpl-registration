package org.bwpl.registration.utils

import static org.fest.assertions.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class DateTimeUtilsTest {

    @Test
    void testIsBeforeIsAfter() {

        DateTime d1 = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)
        DateTime d2 = new DateTime().withYear(2011).withMonthOfYear(5).withDayOfMonth(12)
        DateTime d3 = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)
        DateTimeUtils date1 = new DateTimeUtils(d1)
        assertThat(date1.isBefore(d2)).isFalse()
        assertThat(date1.isAfter(d2)).isTrue()
        assertThat(date1.isBefore(d3)).isFalse()
        assertThat(date1.isAfter(d3)).isFalse()
    }

    @Test
    void testPrint() {

        DateTime dt = new DateTime().withYear(2012).withMonthOfYear(5).withDayOfMonth(12)
        Date d = dt.toDate()
        String str = DateTimeUtils.printDate(d)
        assertThat(str).isEqualTo("12-05-2012")
    }
}
