package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.asa.ASAMemberDataRetrieval
import org.bwpl.registration.asa.ASAMemberDataRetrievalException
import org.bwpl.registration.asa.ASAMemberDataValidationException
import org.bwpl.registration.data.RegistrationData
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.query.RegistrationSearch
import org.bwpl.registration.query.SearchParameters
import org.bwpl.registration.upload.RegistrationUploader
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.validation.*

class RegistrationController {

    SecurityUtils securityUtils
    NavItems nav
    RegistrationUploader registrationUploader
    Validator validator
    RegistrationSearch registrationSearch

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
                                     title: "Add Registration for ${team.nameAndGender}",
                                     team: team])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def add = {

        Team t = Team.get(params.teamId)
        RegistrationData registrationData = RegistrationData.fromFormParams(params)
        String errors = registrationData.getErrors(t)

        if (!errors.isEmpty()) {
            flash.errors = "Error adding registration for $t.name ($t.club.name) - $errors"
            def redirectParams = [teamId: params.teamId, firstName: params["firstName"], lastName: params["lastName"],
                                  role: params["role"], asaNumber: params["asaNumber"], competition: params.competition]
            redirect(action: "create", params: redirectParams)
            return
        }

        Registration r = new Registration()
        r.firstName = registrationData.firstName
        r.lastName = registrationData.lastName
        r.role = registrationData.role
        r.asaNumber = registrationData.asaNumber
        r.updateStatus(securityUtils.currentUser, Action.ADDED, Status.NEW, "")
        t.addToRegistrations(r)
        t.save()

        flash.message = "Registration $r.name for $r.team.name ($r.team.club.name) added."
        redirect(controller: "team", action: "show", id: t.id, params: [competition: params.competition])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def update = {

        Team t = Team.get(params.teamId)
        Registration r = Registration.get(params.id)
        RegistrationData registrationData = RegistrationData.fromFormParams(params)
        String errors = registrationData.getFieldValueErrors()

        if (!errors.isEmpty()) {
            flash.errors = "Error updating registration $r.name for $r.team.name ($r.team.club.name) - $errors"
            redirect(action: "edit", id: params.id, params: [competition: params.competition])
            return
        }

        if (!securityUtils.isCurrentUserRegistrationSecretary()) {

            if (r.statusAsEnum == Status.DELETED) {
                flash.errors = "Cannot update a registration that has been deleted."
                redirect(action: "edit", id: r.id, params: [competition: params.competition])
                return
            }
            if (!r.canUpdate()) {
                flash.errors = "Cannot update a registration that has already been validated."
                redirect(action: "edit", id: r.id, params: [competition: params.competition])
                return
            }
        }

        r.firstName = registrationData.firstName
        r.lastName = registrationData.lastName
        r.role = registrationData.role
        r.asaNumber = registrationData.asaNumber

        if (securityUtils.isCurrentUserRegistrationSecretary()) {
            Status status = Status.fromString(params["status"])
            r.updateStatus(securityUtils.currentUser, Action.VALIDATED, status, params["statusNotes"])
        }
        r.save()

        flash.message = "Registration $r.name for $t.name ($t.club.name) updated."
        redirect(controller: "registration", action: "show", id: r.id, params: [competition: params.competition])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def delete = {

        Registration r = Registration.get(params.id)
        if (securityUtils.isCurrentUserRegistrationSecretary() || r.canUpdate()) {
            r.updateStatus(securityUtils.currentUser, Action.DELETED, Status.DELETED, "")
            r.save()
            flash.message = "Registration $r.name for $r.team.name ($r.team.club.name) deleted."
        }
        else {
            flash.errors = "Cannot delete registration $r.name for $r.team.name ($r.team.club.name) as this registration is or has been validated. Please contact the Registration Secretary."
        }
        redirect(uri: params.targetUri)
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def deleteDeleted = {

        Registration r = Registration.get(params.id)
        long id = r.team.club.id
        if (r.statusAsEnum == Status.DELETED) {
            String msg = "Registration for $r.name for $r.team.name ($r.team.club.name) removed permanently."
            r.team.removeFromRegistrations(r)
            r.delete()
            flash.message = msg
        }
        redirect(controller: "club", action: "show", id: id, params: [competition: params.competition, rfilter: "deleted"])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def undelete = {

        Registration r = Registration.get(params.id)
        if (securityUtils.isCurrentUserRegistrationSecretary() || r.canUpdate()) {
            r.updateStatus(securityUtils.currentUser, Action.UNDELETED, Status.NEW, "")
            r.save()
            flash.message = "Registration $r.name for $r.team.name ($r.team.club.name) undeleted."
        }
        else {
            flash.errors = "Cannot undelete registration $r.name for $r.team.name ($r.team.club.name) as this registration is or has been validated. Please contact the Registration Secretary."
        }
        redirect(uri: params.targetUri)
    }

    @Secured(["ROLE_READ_ONLY"])
    def search = {

        SearchParameters searchParameters = new SearchParameters()
        searchParameters.firstName = params["firstName"]
        searchParameters.lastName = params["lastName"]
        searchParameters.statusChangedDate = params["statusChangedDate"]
        List<Registration> results = registrationSearch.search(searchParameters)
        [user: securityUtils.currentUser,
         navItems: nav.getNavItems(),
         firstName: StringUtils.trimToEmpty(params["firstName"]),
         lastName: StringUtils.trimToEmpty(params["lastName"]),
         statusChangedDate: searchParameters.statusChangedDate.toJavaDate(),
         registrations: results,
         stats: new RegistrationStats(results)]
    }

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        Registration r = Registration.get(params.id)
        boolean canUpdate = securityUtils.canUserUpdate(r.team.club)
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        [user: securityUtils.currentUser,
         title: r.name,
         navItems: nav.getNavItems(),
         registration: r,
         canUpdate: canUpdate,
         isUserRegistrationSecretary: isUserRegistrationSecretary]
    }

    @Secured(["ROLE_READ_ONLY"])
    def export = {

        String dateTimeStamp = BwplDateTime.now.toFileNameDateTimeString()
        String fileName = "bwpl-registrations-${dateTimeStamp}.csv"
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.contentType = "text/csv"
        response.outputStream << Registration.getRegistrationsAsCsvString() << "\n"
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
        redirect(action: "search", params: [competition: params.competition])
    }

    @Secured(["ROLE_CLUB_SECRETARY"])
    def validate = {

        if (params.id) {

            ASAMemberDataRetrieval asaMemberDataRetrieval = new ASAMemberDataRetrieval()
            String asaMemberCheckError = asaMemberDataRetrieval.getServiceError()
            if (!asaMemberCheckError.isEmpty()) {
                flash.errors = asaMemberCheckError
                redirect(uri: params.targetUri)
                return
            }
            Registration r = Registration.get(params.id)
            try {
                validator.validate(r)
            }
            catch (ASAMemberDataRetrievalException e) {
                flash.errors = "ASA Membership Check system is unavailable - $e.message"
            }
            catch (ASAMemberDataValidationException e) {
                flash.errors = "Error reading data from the ASA Membership system - $e.message"
            }
            redirect(uri: params.targetUri)
            return
        }

        ValidationQueue validationQueue = session["validationQueue"]

        if (validationQueue == null) {
            Set<Registration> registrations = getRegistrationsToValidate()
            validationQueue = new ValidationQueue(validator, registrations)
            session["validationQueue"] = validationQueue
        }

        if (validationQueue.isEmpty()) {
            session["validationQueue"] = null
            String msg = "Validation complete - ${validationQueue.toString()}"
            if (validationQueue.asaMemberCheckError.isEmpty()) {
                flash.message = msg
            }
            else {
                flash.errors = msg
            }
            render(msg)
        }
        else {
            validationQueue.process()
            render("Validating --> ${validationQueue.toString()}")
        }
    }

    private Set<Registration> getRegistrationsToValidate() {

        ASAMemberDataRetrieval asaMemberDataRetrieval = new ASAMemberDataRetrieval()
        String asaMemberCheckError = asaMemberDataRetrieval.getServiceError()
        if (!asaMemberCheckError.isEmpty()) return new HashSet<Registration>()

        if (StringUtils.isNotEmpty(params.teamId)) {
            Set<Registration> registrations = Team.get(params.teamId).registrations
            return registrations.findAll {it.statusAsEnum in [Status.NEW, Status.INVALID]}
        }
        if (StringUtils.isNotEmpty(params.clubId)) {
            Competition competition = Competition.findByUrlName(params.competition)
            Set<Registration> registrations = Club.get(params.clubId).getRegistrations(competition)
            return registrations.findAll {it.statusAsEnum in [Status.NEW, Status.INVALID]}
        }
        return new HashSet<Registration>()
    }
}
