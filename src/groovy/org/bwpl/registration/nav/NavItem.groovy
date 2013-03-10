package org.bwpl.registration.nav

class NavItem {

    static final NavItem admin = new NavItem()
    static final NavItem securityManagementConsole = new NavItem()

    static final NavItem clubList = new NavItem()
    static final NavItem searchRegistrations = new NavItem()

    static final NavItem registrationsList = new NavItem()
    static final NavItem invalidRegistrationsList = new NavItem()

    static final NavItem registrationsTabAll = new NavItem()
    static final NavItem registrationsTabInvalid = new NavItem()
    static final NavItem registrationsTabDeleted = new NavItem()

    static {

        admin.with {
            displayName = "Admin"
            controllerName = "registration"
            actionName = "admin"
        }

        securityManagementConsole.with {
            displayName = "Security Management"
            controllerName = "user"

        }

        clubList.with {
            displayName = "Clubs"
            controllerName = "club"
            actionName = "list"
        }

        searchRegistrations.with {
            displayName = "Search"
            controllerName = "registration"
            actionName = "search"
        }

        registrationsList.with {
            displayName = "Registrations"
            controllerName = "registration"
            actionName = "list"
        }

        invalidRegistrationsList.with {
            displayName = "Invalid Registrations"
            controllerName = "registration"
            actionName = "list"
            params = [rfilter: "invalid"]
        }

        registrationsTabAll.with {
            displayName = "All"
            controllerName = "registration"
            actionName = "list"
        }

        registrationsTabInvalid.with {
            displayName = "Invalid"
            controllerName = "registration"
            actionName = "list"
            params = [rfilter: "invalid"]
        }

        registrationsTabDeleted.with {
            displayName = "Deleted"
            controllerName = "registration"
            actionName = "list"
            params = [rfilter: "deleted"]
        }
    }

    static NavItem getUserClub(long clubId, String clubName) {

        NavItem userClub = new NavItem()
        userClub.with {
            displayName = clubName
            controllerName = "club"
            actionName = "show"
            params = [id: clubId.toString()]
        }
        return userClub
    }

    static NavItem getASAEmail(long clubId) {

        NavItem asaEmail = new NavItem()
        asaEmail.with {
            displayName = "ASA Email"
            controllerName = "club"
            actionName = "asaemail"
            params = [id: clubId.toString()]
        }
        return asaEmail
    }

    static NavItem getExportClubRegistrations(long clubId) {

        NavItem exportClubRegistrations = new NavItem()
        exportClubRegistrations.with {
            displayName = "Export"
            controllerName = "club"
            actionName = "export"
            params = [id: clubId.toString()]
        }
        return exportClubRegistrations
    }

    static NavItem getUploadRegistrations(long teamId) {

        NavItem uploadRegistrations = new NavItem()
        uploadRegistrations.with {
            displayName = "Upload"
            controllerName = "team"
            actionName = "upload"
            params = [id: teamId.toString()]
        }
        return uploadRegistrations
    }

    static NavItem getAddRegistration(long teamId) {

        NavItem addRegistration = new NavItem()
        addRegistration.with {
            displayName = "Add"
            controllerName = "registration"
            actionName = "create"
            params = [teamId: teamId.toString()]
        }
        return addRegistration
    }

    static NavItem getTeamRegistrations(long teamId, String teamName) {

        NavItem teamRegistrations = new NavItem()
        teamRegistrations.with {
            displayName = "$teamName Registrations"
            controllerName = "team"
            actionName = "show"
            params = [id: teamId.toString()]
        }
        return teamRegistrations
    }

    static NavItem getDeleteAllRegistrations(long teamId) {

        NavItem deleteAllRegistrations = new NavItem()
        deleteAllRegistrations.with {
            displayName = "Delete All"
            controllerName = "team"
            actionName = "deleteRegistrations"
            params = [id: teamId.toString()]
            onclick = "return confirm('Are you sure you want to delete all team registrations?');"
        }
        return deleteAllRegistrations
    }

    String displayName
    String controllerName
    String actionName
    String onclick = ""
    Map<String, String> params = new HashMap<String, String>()
}
