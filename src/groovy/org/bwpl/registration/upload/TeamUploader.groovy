package org.bwpl.registration.upload

import org.bwpl.registration.Competition
import org.springframework.web.multipart.MultipartFile

class TeamUploader {

    void upload(Competition competition, MultipartFile file) {

        CsvReader reader = getCompetitionTeamCsvReader(competition)
        reader.readFromMultipartFile(file)
    }

    void uploadFile(Competition competition, File file) {

        CsvReader reader = getCompetitionTeamCsvReader(competition)
        reader.readFromFile(file)
    }

    private static CsvReader getCompetitionTeamCsvReader(Competition competition) {

        TeamDataHandler teamDataHandler = new TeamDataHandler()
        teamDataHandler.competition = competition
        return new CsvReader(contentHandler: teamDataHandler)
    }
}
