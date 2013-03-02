package org.bwpl.registration.upload

import org.springframework.web.multipart.MultipartFile

class UserUploader {

    void upload(MultipartFile f) {

        CsvReader reader = new CsvReader(contentHandler: new UserDataHandler())
        reader.readFromMultipartFile(f)
    }

    void uploadFile(File f) {

        CsvReader reader = new CsvReader(contentHandler: new UserDataHandler())
        reader.readFromFile(f)
    }
}
