package org.bwpl.registration.utils

import grails.plugin.mail.MailService
import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.validation.Status

class EmailUtils {

    private static final String SYS_ADMIN_EMAIL = "reddicj@gmail.com"

    BwplProperties bwplProperties
    MailService mailService

    void emailError(String errorTitle, String errorDetails) {

        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to SYS_ADMIN_EMAIL
            subject "BWPL Registration System Error: $errorTitle"
            text errorDetails
        }
    }

    void emailRegistrationsExport() {

        BwplDateTime now = BwplDateTime.now
        String dateTimeStamp = now.toFileNameDateTimeString()
        mailService.sendMail {

            multipart true
            from SYS_ADMIN_EMAIL
            to bwplProperties.emailListWeekendRegistrationsReport
            subject "BWPL Registration System - Registrations List"
            text "BWPL registrations list as of ${now.toDateString()} - see attached csv file."
            attach "bwpl-registrations-${dateTimeStamp}.csv", "application/zip", Registration.getRegistrationsAsCsvString().bytes
        }
    }

    void emailDataExport() {

        String dateTimeStamp = BwplDateTime.now.toFileNameDateTimeString()
        mailService.sendMail {

            multipart true
            from SYS_ADMIN_EMAIL
            to bwplProperties.emailListNightlyDataExport
            subject "BWPL Registration System - Data Export"
            text "BWPL Registration System export - see attached zip file."
            attach "bwpl-data-${dateTimeStamp}.zip", "application/zip", ZipUtils.exportDataZipFileAsByteArray()
        }
    }

    void emailInvalidatedRegistrations() {

        Club.list().each { club ->

            Set<Registration> invalidated = Registration.findAll {
                team.club == club && status == Status.INVALID.toString() && prevStatus == Status.VALID.toString()
            }
            if (!invalidated.isEmpty()) {
                club.secretaries.each { sec ->
                    emailInvalidatedRegistrationsForClub(club, sec.email, invalidated)
                }
            }
        }
    }

    void emailValidatedRegistrations() {

        Club.list().each { club ->

            Set<Registration> validated = Registration.findAll {
                team.club == club && status == Status.VALID.toString() && prevStatus != Status.VALID.toString()
            }
            if (!validated.isEmpty()) {
                club.secretaries.each { sec ->
                    emailValidatedRegistrationsForClub(club, sec.email, validated)
                }
            }
        }
    }

    private void emailInvalidatedRegistrationsForClub(Club club, String emailAddress, Set<Registration> registrations) {

        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to emailAddress
            subject "BWPL Registration System - Invalidated Registrations for $club.name"
            text getEmailBody(club, registrations, "invalidated")
        }
    }

    private void emailValidatedRegistrationsForClub(Club club, String emailAddress, Set<Registration> registrations) {

        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to emailAddress
            subject "BWPL Registration System - Validated Registrations for $club.name"
            text getEmailBody(club, registrations, "validated")
        }
    }

    private static String getEmailBody(Club club, Set<Registration> registrations, String message) {

        StringBuilder sb = new StringBuilder()
        sb << "The BWPL Registration System has $message the following registrations for $club.name:\n\n"
        List<Registration> sortedList = new ArrayList<Registration>(registrations)
        sortedList.sort{it.name}
        sortedList.each { r ->
            sb << "$r.name, $r.asaNumber, $r.team.name ($r.team.gender)"
            if (r.statusAsEnum == Status.INVALID) {
                sb << " - ${r.getStatusNote()}"
            }
            sb << "\n"
        }
        return sb.toString()
    }
}
