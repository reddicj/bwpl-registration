package org.bwpl.registration.upload

import org.bwpl.registration.Team
import org.bwpl.registration.utils.SecurityUtils
import org.springframework.web.multipart.MultipartFile

class RegistrationUploader {

    SecurityUtils securityUtils

    void upload(Team team, MultipartFile f) {

        CsvReader reader = getTeamRegistrationsCsvReader(team)
        reader.readFromMultipartFile(f)
    }

    void upload(MultipartFile f) {

        CsvReader reader = getRegistrationsCsvReader()
        reader.readFromMultipartFile(f)
    }

    private CsvReader getRegistrationsCsvReader() {

        RegistrationDataHandler registrationDataHandler = new RegistrationDataHandler()
        registrationDataHandler.currentUser = securityUtils.currentUser
        return new CsvReader(contentHandler: registrationDataHandler)
    }

    private CsvReader getTeamRegistrationsCsvReader(Team team) {

        RegistrationTeamDataHandler registrationTeamDataHandler = new RegistrationTeamDataHandler()
        registrationTeamDataHandler.currentUser = securityUtils.currentUser
        registrationTeamDataHandler.team = team
        return new CsvReader(contentHandler: registrationTeamDataHandler)
    }
}