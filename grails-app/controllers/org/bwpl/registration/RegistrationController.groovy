package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.validation.RegistrationStats
import org.bwpl.registration.validation.ValidationQueue
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.utils.CsvWriter

import org.bwpl.registration.validation.Status
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Validator
import org.apache.commons.lang.WordUtils
import grails.plugins.springsecurity.Secured
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.upload.RegistrationUploader

class RegistrationController {

    SecurityUtils securityUtils
    NavItems nav
    RegistrationUploader registrationUploader
    Validator validator

    @Secured(["ROLE_READ_ONLY"])
    def admin = {

        if (securityUtils.isCurrentUserRegistrationSecretary()) {
            [user: securityUtils.currentUser,
             navItems: nav.getNavItems(),
             stats: new RegistrationStats(Registration.list())]
        }
        else {
            [user: securityUtils.currentUser, navItems: nav.getNavItems()]
        }
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def edit = {

        Registration registration = Registration.get(params.id)
        Team team = registration.team
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        [user: securityUtils.currentUser,
         navItems: nav.getNavItems(),
         title: "Edit Registration for ${registration.team.nameAndGender}",
         r: registration,
         team: team,
         isUserRegistrationSecretary: isUserRegistrationSecretary]
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def create = {

        Team team = Team.get(params.teamId)
        render(view: "edit", model: [user: securityUtils.currentUser,
                                     navItems: nav.getNavItems(),
                                     title: "Add Registration  for ${team.nameAndGender}",
                                     team: team])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def add = {

        Team t = Team.get(params.teamId)
        Registration r = new Registration()
        r.firstName = params["firstName"]
        r.lastName = params["lastName"]
        r.role = params["role"]
        r.asaNumber = Integer.parseInt(params["asaNumber"])
        r.updateStatus(securityUtils.currentUser, Action.ADDED, Status.INVALID, "")
        t.addToRegistrations(r)
        t.save()
        redirect(controller: "team", action: "show", id: t.id)
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def update = {

        Team t = Team.get(params.teamId)
        Registration r = Registration.get(params.id)

        if (!securityUtils.isCurrentUserRegistrationSecretary()) {

            if (r.statusAsEnum == Status.DELETED) {
                flash.errors = "Cannot update a registration that has been deleted."
                redirect(action: "edit", id: r.id)
                return
            }
            if (r.hasBeenValidated()) {
                flash.errors = "Cannot update a registration that has already been validated."
                redirect(action: "edit", id: r.id)
                return
            }
        }

        if (StringUtils.isNotBlank(params["firstName"])) r.firstName = params["firstName"]
        if (StringUtils.isNotBlank(params["lastName"])) r.lastName = params["lastName"]

        if (securityUtils.isCurrentUserRegistrationSecretary()) {
            Status status = Status.fromString(params["status"])
            r.updateStatus(securityUtils.currentUser, Action.VALIDATED, status, params["statusNotes"])
        }
        r.save()
        redirect(controller: "team", action: "show", id: t.id)
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def delete = {

        Registration r = Registration.get(params.id)
        if (securityUtils.isCurrentUserRegistrationSecretary() || (!r.hasBeenValidated())) {
            r.updateStatus(securityUtils.currentUser, Action.DELETED, Status.DELETED, "")
            r.save()
        }
        else {
            flash.errors = "Cannot delete registration ${r.name} for ${r.team.name} (${r.team.club.name}) as this registration is or has been validated. Please contact the Registration Secretary."
        }
        redirect(uri: params.targetUri)
    }

    @Secured(["ROLE_READ_ONLY"])
    def list = {

        List<Registration> registrations = getRegistrations(params.rfilter)
        boolean canUpdate = securityUtils.isCurrentUserRegistrationSecretary()

        [user: securityUtils.currentUser,
         navItems: nav.getNavItems(),
         subNavItems: nav.getRegistrationsNavItems(),
         tabItems: nav.getRegistrationsTabs(),
         registrations: registrations,
         stats: new RegistrationStats(registrations),
         canUpdate: canUpdate,
         doDisplayValidateButton: doDisplayValidateButton(canUpdate, params.rfilter)]
    }

    @Secured(["ROLE_READ_ONLY"])
    def search = {

        List<Registration> results = getSearchResults(params["firstName"], params["lastName"])
        [user: securityUtils.currentUser,
         navItems: nav.getNavItems(),
         firstName: StringUtils.trimToEmpty(params["firstName"]),
         lastName: StringUtils.trimToEmpty(params["lastName"]),
         registrations: results,
         stats: new RegistrationStats(results)]
    }

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        Registration r = Registration.get(params.id)
        boolean canUpdate = securityUtils.canUserUpdate(r.team.club)
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        [user: securityUtils.currentUser,
         title: "$r.name",
         navItems: nav.getNavItems(),
         registration: r,
         canUpdate: canUpdate,
         isUserRegistrationSecretary: isUserRegistrationSecretary]
    }

    @Secured(["ROLE_READ_ONLY"])
    def export = {

        List<Club> clubs = Club.list()
        clubs.sort{it.name}
        String dateTimeStamp = DateTimeUtils.printFileNameDateTime(new Date())
        String fileName = "bwpl-registrations-${dateTimeStamp}.csv"
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.contentType = "text/csv"
        response.outputStream << CsvWriter.csvFieldNames << "\n"
        clubs.each {
            response.outputStream << CsvWriter.getRegistrationsAsCsvString(it) << "\n"
        }
        response.flushBuffer()
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def upload = {
        [user: securityUtils.currentUser,
         title: "Upload All Registrations",
         navItems: nav.getNavItems()]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def uploadRegistrations = {

        def f = request.getFile("registrations")
        try {
            registrationUploader.upload(f)
            flash.message = "Successfully uploaded registrations"
        } catch (UploadException e) {
            flash.errors = e.message
        }
        redirect(action: "search")
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def validate = {

        ValidationQueue validationQueue = session["validationQueue"]

        if (validationQueue == null) {

            Set<Registration> registrations = getRegistrationsToValidate()
            validationQueue = new ValidationQueue(validator, registrations)
            session["validationQueue"] = validationQueue
        }

        if (validationQueue.isEmpty()) {
            session["validationQueue"] = null
            render("Validation complete --> ${validationQueue.toString()}")
        }
        else {
            validationQueue.process()
            render("Validating --> ${validationQueue.toString()}")
        }
    }

    private Set<Registration> getRegistrationsToValidate() {

        if (StringUtils.isNotEmpty(params.teamId)) {
            Set<Registration> registrations = Team.get(params.teamId).registrations
            return registrations.findAll {it.statusAsEnum == Status.INVALID}
        }
        if (StringUtils.isNotEmpty(params.clubId)) {
            Set<Registration> registrations = Club.get(params.clubId).registrations
            return registrations.findAll {it.statusAsEnum == Status.INVALID}
        }

        if (SpringSecurityUtils.ifAllGranted("ROLE_REGISTRATION_SECRETARY")) {
            return Registration.findAllByStatus(Status.INVALID.toString())
        }
        else {
            return new HashSet<Registration>()
        }
    }

    private static List<Registration> getRegistrations(String registrationFilter) {

        if ("invalid" == registrationFilter) {
            return Registration.findAllByStatus(Status.INVALID.toString()).sort{it.name}
        }
        else if ("valid" == registrationFilter) {
            return Registration.findAllByStatus(Status.VALID.toString()).sort{it.name}
        }
        else if ("deleted" == registrationFilter) {
            return Registration.findAllByStatus(Status.DELETED.toString()).sort{it.name}
        }
        else {
            return Registration.findAllByStatusOrStatus(Status.VALID.toString(), Status.INVALID.toString()).sort{it.name}
        }
    }

    private static boolean doDisplayValidateButton(boolean canUserUpdate, String rfilter) {
        return canUserUpdate && !StringUtils.equals(rfilter, "deleted")
    }

    private static List<Registration> getSearchResults(String firstName, String lastName) {

        List<Registration> results = null
        if (StringUtils.isNotBlank(firstName)) {
            firstName = WordUtils.capitalize(firstName.trim())
            if (StringUtils.isNotBlank(lastName)) {
                lastName = WordUtils.capitalize(lastName.trim())
                results = Registration.findAllByFirstNameAndLastName(firstName, lastName)
            }
            else {
                results = Registration.findAllByFirstName(firstName)
            }
        }
        else if (StringUtils.isNotBlank(lastName)) {
            lastName = WordUtils.capitalize(lastName.trim())
            results = Registration.findAllByLastName(lastName)
        }
        else {
            results = []
        }
        results.sort{it.name}
        return results
    }
}
