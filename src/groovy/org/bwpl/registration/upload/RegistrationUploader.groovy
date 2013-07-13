package org.bwpl.registration.upload

import org.bwpl.registration.Team
import org.bwpl.registration.User
import org.bwpl.registration.utils.SecurityUtils
import org.springframework.web.multipart.MultipartFile

class RegistrationUploader {

    SecurityUtils securityUtils

    void upload(Team team, MultipartFile f) {

        CsvReader reader = getTeamRegistrationsCsvReader(team)
        reader.readFromMultipartFile(f)
    }

    void upload(Team team, File f) {

        CsvReader reader = getTeamRegistrationsCsvReader(team)
        reader.readFromFile(f)
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
        registrationTeamDataHandler.currentUser = getCurrentUser()
        registrationTeamDataHandler.team = team
        return new CsvReader(contentHandler: registrationTeamDataHandler)
    }

    private User getCurrentUser() {

        if (securityUtils == null) return User.sys
        return securityUtils.currentUser
    }
}