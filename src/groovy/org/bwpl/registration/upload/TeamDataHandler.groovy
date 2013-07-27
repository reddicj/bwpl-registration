package org.bwpl.registration.upload

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Club
import org.bwpl.registration.Team

import static org.bwpl.registration.utils.ValidationUtils.*

class TeamDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Club name", "ASA Club name", "Team name", "Gender (M/F)", "Division"]

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(values)
        if (!errors.isEmpty()) {
            throw new UploadException("Line $lineNumber: $errors")
        }

        String clubName = WordUtils.capitalizeFully(StringUtils.trimToEmpty(values[0]))
        String asaClubName = WordUtils.capitalizeFully(StringUtils.trimToEmpty(values[1]))
        String teamName = WordUtils.capitalizeFully(StringUtils.trimToEmpty(values[2]))
        boolean isMale = "M".equalsIgnoreCase(StringUtils.trimToEmpty(values[3]))
        int division = Integer.parseInt(StringUtils.trimToEmpty(values[4]))

        Club club = Club.findByName(clubName)
        if (club && club.hasTeam(teamName)) {
            return
        }
        if (!club) {
            club = new Club()
            club.name = clubName
            club.asaName = asaClubName
            club.save()
        }
        Team team = new Team()
        team.name = teamName
        team.isMale = isMale
        team.division = division
        club.addToTeams(team)
        club.save(flush: true)
    }

    private String getErrors(String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            return "$expected, $actual"
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue("Club name", values[0], errors)
            checkValueContainsValidClubTeamNameCharacters("Club name", values[0], errors)

            checkForNullOrEmptyValue("ASA club name", values[1], errors)
            checkValueContainsValidClubTeamNameCharacters("ASA club name", values[1], errors)

            checkForNullOrEmptyValue("Team name", values[2], errors)
            checkValueContainsValidClubTeamNameCharacters("Team name", values[2], errors)

            checkForNullOrEmptyValue("Gender", values[3], errors)
            checkValueInList("Gender", values[3], ["M", "F"], errors)

            checkForNullOrEmptyValue("Division", values[4], errors)
            checkValueIsNumeric("Division", values[4], errors)

            return errors.join(", ")
        }
    }
}
