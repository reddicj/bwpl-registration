package org.bwpl.registration.upload

interface CsvHandler {

    void processTokens(int lineNumber, String[] tokens)
}