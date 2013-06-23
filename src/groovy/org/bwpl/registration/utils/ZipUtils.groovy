package org.bwpl.registration.utils

import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.io.IOUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.User

class ZipUtils {

    private final ArchiveOutputStream archiveOutputStream

    ZipUtils(OutputStream outputStream) {
        archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream("zip", outputStream)
    }

    static byte[] exportDataZipFileAsByteArray() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ZipUtils zipUtils = new ZipUtils(byteArrayOutputStream)
        zipUtils.with {
            addArchiveEntry("users.csv", User.getUsersAsCsvString())
            addArchiveEntry("teams.csv", Team.getTeamsAsCsvString())
            addArchiveEntry("registrations.csv", Registration.getRegistrationsAsCsvString())
            close()
        }
        return byteArrayOutputStream.toByteArray()
    }

    void addArchiveEntry(String filename, String content) {

        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry(filename))
        IOUtils.write(content, archiveOutputStream)
        archiveOutputStream.closeArchiveEntry()
    }

    void close() {
        archiveOutputStream.close()
    }
}
