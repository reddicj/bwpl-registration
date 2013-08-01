package org.bwpl.registration.asa

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.utils.StringMunger
import org.bwpl.registration.utils.ValidationUtils
import org.joda.time.DateTime

class ASAMemberData {

    final int asaNumber
    String name = ""
    Date dateOfBirth
    Boolean isMale
    String membershipCategory
    List<ASAMemberClub> clubs = []

    static ASAMemberData fromHtml(int asaNumber, GPathResult html) {

        ASAMemberData asaMemberData = new ASAMemberData(asaNumber)
        GPathResult element = html.BODY.depthFirst().find {it.text() == "Fee Paying Club"}
        GPathResult divElement = element.parent().parent().parent()
        GPathResult tableElement = divElement.TABLE[0]
        asaMemberData.setName(tableElement.TR[0].TD[1].text())
        asaMemberData.setDateOfBirth(tableElement.TR[1].TD[1].text())
        asaMemberData.setGender(tableElement.TR[1].TD[3].text())
        asaMemberData.setMembershipCategory(tableElement.TR[2].TD[3].text())
        tableElement = divElement.TABLE[1]
        for (int i = 1; i < tableElement.TR.size(); i++) {
            asaMemberData.addClub(tableElement.TR[i].TD[1].text(), tableElement.TR[i].TD[3].text(), tableElement.TR[i].TD[2].text())
        }
        return asaMemberData
    }

    ASAMemberData(int asaNumber) {
        this.asaNumber = asaNumber
    }

    void setName(String name) {

        String n = StringUtils.trimToEmpty(name)
        if (StringUtils.isEmpty(n)) {
            throw new ASAMemberDataValidationException("Name data is empty")
        }
        this.name = n
    }

    void setDateOfBirth(String dateOfBirth) {

        String dob = StringUtils.trimToEmpty(dateOfBirth)
        if (StringUtils.isEmpty(dob)) {
            this.dateOfBirth = null
            return
        }
        if (!ValidationUtils.isValidAsaDateOfBirth(dob)) {
            throw new ASAMemberDataValidationException("Invalid date format: [$dob]")
        }
        DateTime dt = DateTimeUtils.parseASADateOfBirth(dob)
        this.dateOfBirth = dt.toDate()
    }

    void setGender(String gender) {

        String g = StringUtils.trimToEmpty(gender)
        if (StringUtils.isEmpty(g)) {
            throw new ASAMemberDataValidationException("Gender data is empty")
        }
        if (!ValidationUtils.isValidGenderData(g)) {
            throw new ASAMemberDataValidationException("Invalid gender data: [$g]")
        }
        isMale = StringUtils.startsWithIgnoreCase(g, "M")
    }

    void setMembershipCategory(String category) {

        String c = StringUtils.trimToEmpty(category)
        if (StringUtils.isEmpty(category)) {
            throw new ASAMemberDataValidationException("Membership category data is empty")
        }
        this.membershipCategory = c
    }

    void addClub(String clubName, String fromDate, String membership) {

        String cn = StringUtils.trimToEmpty(clubName)
        String fd = StringUtils.trimToEmpty(fromDate)
        String m = StringUtils.trimToEmpty(membership)
        if (StringUtils.isEmpty(cn) || StringUtils.isEmpty(fd) || StringUtils.isEmpty(m)) {
            throw new ASAMemberDataValidationException("Club name, from date or membership is empty")
        }
        ASAMemberClub club = new ASAMemberClub(cn, fd, m)
        clubs << club
    }

    boolean isNameMatch(String firstName, String lastName) {

        if (StringUtils.isEmpty(firstName)) return false
        if (StringUtils.isEmpty(lastName)) return false
        String mungedFirstName = StringMunger.munge(firstName.toLowerCase())
        String mungedLastName = StringMunger.munge(lastName.toLowerCase())
        String mungedName = StringMunger.munge(name.toLowerCase())
        if (!StringUtils.containsIgnoreCase(mungedName, mungedLastName)) return false
        String nameSubStringBeforeLastNameMatch = StringUtils.substringBefore(mungedName, mungedLastName)
        return StringUtils.containsIgnoreCase(nameSubStringBeforeLastNameMatch, mungedFirstName)
    }

    boolean isCorrectlyRegisteredWithClub(String clubName, String role) {

        for (ASAMemberClub asaMemberClub : clubs) {
            if (asaMemberClub.name.startsWith(clubName)) {
                if (role != "Player") return true
                else return asaMemberClub.isMember
            }
        }
        return false
    }

    boolean isValidMembershipCategory(String role) {

        if (role == "Player") {
            return isValidPlayerCategory()
        }
        else {
            return isValidNonPlayerCategory()
        }
    }

    boolean isValidPlayerCategory() {
        return "ASA Cat 2".equals(membershipCategory) ||
               "SASA Swimmer".equals(membershipCategory) ||
               "WASA Cat 2".equals(membershipCategory)
    }

    boolean isValidNonPlayerCategory() {
        return isValidPlayerCategory() ||
               "ASA Cat 1".equals(membershipCategory) ||
               "ASA Cat 3".equals(membershipCategory) ||
               "SASA Non-Swim Teach/Coach".equals(membershipCategory) ||
               "WASA Cat 3".equals(membershipCategory)
    }
}
