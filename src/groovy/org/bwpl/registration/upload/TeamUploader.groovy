package org.bwpl.registration.upload

import org.springframework.web.multipart.MultipartFile

class TeamUploader {

    void upload(MultipartFile file) {

        CsvReader reader = new CsvReader(contentHandler: new TeamDataHandler())
        reader.readFromMultipartFile(file)
    }

    void uploadFile(File file) {

        CsvReader reader = new CsvReader(contentHandler: new TeamDataHandler())
        reader.readFromFile(file)
    }
}
