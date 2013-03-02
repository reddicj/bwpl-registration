package org.bwpl.registration.upload

import org.bwpl.registration.Club
import org.bwpl.registration.Team

import static org.bwpl.registration.utils.ValidationUtils.*

class TeamDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Club name", "ASA Club name", "Team name", "Gender (M/F)", "Division"]

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(lineNumber, values)
        if (!errors.isEmpty()) {
            throw new UploadException("Error reading team data --> $errors")
        }

        String clubName = values[0]
        String asaClubName = values[1]
        String teamName = values[2]
        boolean isMale = "M".equalsIgnoreCase(values[3])
        int division = Integer.parseInt(values[4])

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
        club.save()
    }

    private String getErrors(int lineNumber, String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            return "Line $lineNumber: $expected, $actual"
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue(lineNumber, "Club name", values[0], errors)
            checkValueIsAlphaNumericSpace(lineNumber, "Club name", values[0], errors)

            checkForNullOrEmptyValue(lineNumber, "ASA club name", values[1], errors)
            checkValueContainsValidNameCharacters(lineNumber, "ASA club name", values[1], errors)

            checkForNullOrEmptyValue(lineNumber, "Team name", values[2], errors)
            checkValueContainsValidNameCharacters(lineNumber, "Team name", values[2], errors)

            checkForNullOrEmptyValue(lineNumber, "Gender", values[3], errors)
            checkValueInList(lineNumber, "Gender", values[3], ["M", "F"], errors)

            checkForNullOrEmptyValue(lineNumber, "Division", values[4], errors)
            checkValueIsNumeric(lineNumber, "Division", values[4], errors)

            return errors.join(", ")
        }
    }
}
