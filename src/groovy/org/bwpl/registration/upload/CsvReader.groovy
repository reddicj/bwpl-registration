package org.bwpl.registration.upload

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.text.StrMatcher
import org.apache.commons.lang.text.StrTokenizer
import org.bwpl.registration.utils.ValidationUtils
import org.springframework.web.multipart.MultipartFile

class CsvReader {

    CsvHandler contentHandler

    void readFromMultipartFile(MultipartFile f) {

        List<String> errors = []
        ValidationUtils.checkIsValidCsvFile(f, errors)
        if (!errors.isEmpty()) {
            throw new UploadException("Error uploading club data. Errors = ${errors.join(" / ")}")
        }
        String data = new String(f.bytes, "UTF-8")
        readData(data)
    }

    void readFromFile(File f) {
        readData(f.text)
    }

    private void readData(String data) {

        int lineNumber = 0
        int countOfSuccess = 0
        StringBuilder errors = new StringBuilder()

        data.eachLine {

            lineNumber++
            if (StringUtils.isNotBlank(it)) {

                String line = it.trim()
                String[] values = getCsvTokens(line)

                try {
                    contentHandler.processTokens(lineNumber, values)
                    countOfSuccess++
                }
                catch (UploadException e) {
                    if (errors.size() > 0) errors << " / "
                    errors << e.message
                }
            }
        }

        if (errors.size() > 0) {
            throw new UploadException("Uploaded $countOfSuccess registrations successfully, but csv file errors: ${errors.toString()}")
        }
    }

    static String[] getCsvTokens(String str) {

        StrTokenizer tokenizer = StrTokenizer.getCSVInstance(str)
        tokenizer.setTrimmerMatcher(StrMatcher.spaceMatcher())
        return tokenizer.getTokenArray()
    }
}