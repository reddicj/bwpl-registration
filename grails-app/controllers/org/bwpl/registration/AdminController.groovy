package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.upload.UserUploader
import org.bwpl.registration.validation.RegistrationStats

class AdminController {

    static defaultAction = "show"

    SecurityUtils securityUtils
    NavItems nav

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        if (securityUtils.isCurrentUserRegistrationSecretary()) {
            [user: securityUtils.currentUser,
                    navItems: nav.getNavItems(),
                    stats: new RegistrationStats(Registration.list())]
        }
        else {
            [user: securityUtils.currentUser, navItems: nav.getNavItems()]
        }
    }

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
}
