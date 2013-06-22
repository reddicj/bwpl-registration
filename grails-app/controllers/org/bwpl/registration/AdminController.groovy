package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.upload.UserUploader

class AdminController {

    SecurityUtils securityUtils
    NavItems nav

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def upload = {
        [user: securityUtils.currentUser,
               title: "Upload User data",
               navItems: nav.getNavItems()]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def uploadUsers = {

        def f = request.getFile("users")
        try {
            UserUploader userUploader = new UserUploader()
            userUploader.upload(f)
            flash.message = "Successfully uploaded user data"
        } catch (UploadException e) {
            flash.errors = e.message
        }
        redirect(controller: "registration", action: "admin")
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def export = {

        String dateTimeStamp = DateTimeUtils.printFileNameDateTime(new Date())
        String fileName = "bwpl-data-${dateTimeStamp}.csv"
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.contentType = "text/csv"
        response.with {
            outputStream << "[Users]\n"
            outputStream << User.getUsersAsCsvString() << "\n\n"
            outputStream << "[Teams]\n"
            outputStream << Team.getTeamsAsCsvString() << "\n\n"
            outputStream << "[Registrations]\n"
            outputStream << Registration.getRegistrationsAsCsvString() << "\n\n"
            outputStream << "[Registration Status Histories]\n"
            outputStream << Registration.getRegistrationStatusEntriesAsCsvString()
        }
        response.flushBuffer()
    }
}
