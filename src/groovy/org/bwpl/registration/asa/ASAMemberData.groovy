package org.bwpl.registration.asa

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.utils.ValidationUtils
import org.joda.time.DateTime

class ASAMemberData {

    final int asaNumber
    String name = ""
    Date dateOfBirth
    Boolean isMale
    String membershipCategory
    List<ASAMemberClub> clubs = []

    ASAMemberData(int asaNumber) {
        this.asaNumber = asaNumber
    }

    void setName(String name) {

        String n = StringUtils.trimToEmpty(name)
        if (StringUtils.isEmpty(n)) {
            throw new ASAMemberDataRetrievalException("Name data is empty")
        }
        this.name = n
    }

    void setDateOfBirth(String dateOfBirth) {

        if (StringUtils.isEmpty(dateOfBirth)) {
            this.dateOfBirth = null
            return
        }
        DateTime dt = DateTimeUtils.parseASADateOfBirth(dateOfBirth)
        this.dateOfBirth = dt.toDate()
    }

    void setGender(String gender) {

        String g = StringUtils.trimToEmpty(gender)
        if (StringUtils.isEmpty(g)) {
            throw new ASAMemberDataRetrievalException("Gender data is empty")
        }
        if (!ValidationUtils.isValidGenderData(g)) {
            throw new ASAMemberDataRetrievalException("Invalid gender data: [$g]")
        }
        isMale = StringUtils.startsWithIgnoreCase(g, "M")
    }

    void setMembershipCategory(String category) {

        String c = StringUtils.trimToEmpty(category)
        if (StringUtils.isEmpty(category)) {
            throw new ASAMemberDataRetrievalException("Membership category data is empty")
        }
        this.membershipCategory = c
    }

    void addClub(String clubName, String fromDate, String membership) {

        String cn = StringUtils.trimToEmpty(clubName)
        String fd = StringUtils.trimToEmpty(fromDate)
        String m = StringUtils.trimToEmpty(membership)
        if (StringUtils.isEmpty(cn) || StringUtils.isEmpty(fd) || StringUtils.isEmpty(m)) {
            throw new ASAMemberDataRetrievalException("Club name, from date or membership is empty")
        }
        ASAMemberClub club = new ASAMemberClub(cn, fd, m)
        clubs << club
    }

    boolean isNameMatch(String firstName, String lastName) {

        if (!StringUtils.containsIgnoreCase(name, lastName)) return false
        String nameSubStringBeforeLastNameMatch = StringUtils.substringBefore(name, lastName)
        return StringUtils.containsIgnoreCase(nameSubStringBeforeLastNameMatch, firstName)
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
               "ASA Cat 3".equals(membershipCategory) ||
               "WASA Cat 3".equals(membershipCategory)
    }
}
