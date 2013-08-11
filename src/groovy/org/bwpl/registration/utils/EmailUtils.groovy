package org.bwpl.registration.utils

import grails.plugin.mail.MailService
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.validation.Status

class EmailUtils {

    private static final String SYS_ADMIN_EMAIL = "reddicj@gmail.com"

    BwplProperties bwplProperties
    MailService mailService

    void emailError(String errorTitle, String errorDetails) {

        if (StringUtils.isBlank(errorDetails)) return
        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to bwplProperties.emailListWeeklyValidationReport
            subject "BWPL Registration System Error: $errorTitle"
            text errorDetails
        }
    }

    void emailValidationSummary(String errors) {

        Set<Registration> validated = Registration.findAll {
            status == Status.VALID.toString() && prevStatus != Status.VALID.toString()
        }
        Set<Registration> invalidated = Registration.findAll {
            status == Status.INVALID.toString() && prevStatus == Status.VALID.toString()
        }

        StringBuilder emailBody = new StringBuilder("Weekly validation completed successfully.\n\n")
        if (errors.isEmpty() && invalidated.isEmpty() && validated.isEmpty()) {
            emailBody << "No errors and zero registrations invalidated/validated.\n\n"
        }
        if (!errors.isEmpty()) emailBody << "ASA Online Membership Check errors:\n" << errors << "\n\n"
        if (!invalidated.isEmpty()) emailBody << getValidationReportEmailBody(invalidated, "invalidated") << "\n\n"
        if (!validated.isEmpty()) emailBody << getValidationReportEmailBody(validated, "validated")

        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to bwplProperties.emailListWeeklyValidationReport
            subject "BWPL Registration System - Weekly Validation Report"
            text emailBody.toString().trim()
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

        String emailBody = getValidationReportEmailBody(club, registrations, "invalidated")
        if (StringUtils.isBlank(emailBody)) return
        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to emailAddress
            subject "BWPL Registration System - Invalidated Registrations for $club.name"
            text getValidationReportEmailBody(club, registrations, "invalidated")
        }
    }

    private void emailValidatedRegistrationsForClub(Club club, String emailAddress, Set<Registration> registrations) {

        String emailBody = getValidationReportEmailBody(club, registrations, "validated")
        if (StringUtils.isBlank(emailBody)) return
        mailService.sendMail {
            from SYS_ADMIN_EMAIL
            to emailAddress
            subject "BWPL Registration System - Validated Registrations for $club.name"
            text getValidationReportEmailBody(club, registrations, "validated")
        }
    }

    private static String getValidationReportEmailBody(Club club, Set<Registration> registrations, String message) {

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

    private static String getValidationReportEmailBody(Set<Registration> registrations, String message) {

        StringBuilder sb = new StringBuilder()
        sb << "The following registrations have been $message:\n\n"
        List<Registration> sortedList = new ArrayList<Registration>(registrations)
        OrderBy<Registration> orderByTeamThenName = new OrderBy<Registration>([{it.team.name}, {it.name}])
        sortedList.sort(orderByTeamThenName)
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
