import org.bwpl.registration.UserRole
import org.bwpl.registration.User
import org.bwpl.registration.Role
import org.bwpl.registration.upload.TeamUploader
import org.bwpl.registration.Club

class BootStrap {

    def init = { servletContext ->

        environments {

            production {

                addRoles()
                addRegSec()
            }

            development {

                addClubData()
                addRoles()
                addRegSec()
//                addClubSec("reddicj@gmail.com", "Polytechnic")
//                addReadOnlyUser("chris.ducker@bwpl.org")
            }
        }
    }

    def destroy = {
    }

    private static void addClubData() {

        File f = new File("/home/reddicj/IdeaProjects/bwpl-registration/data/clubs.csv")
        TeamUploader teamUploader = new TeamUploader()
        teamUploader.uploadFile(f)
    }

    private static void addRegSec() {

        User regSecUser = User.findByUsername("admin@gmail.com")
        if (regSecUser == null) {

            Role regSecRole = Role.findByAuthority("ROLE_REGISTRATION_SECRETARY")
            Role clubSecRole = Role.findByAuthority("ROLE_CLUB_SECRETARY")
            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            regSecUser = new User(username: "admin@gmail.com", enabled: true, password: "password").save(flush: true)
            UserRole.create(regSecUser, regSecRole, true)
            UserRole.create(regSecUser, clubSecRole, true)
            UserRole.create(regSecUser, readOnlyRole, true)
        }
    }

    private static void addClubSec(String username, String clubName) {

        User clubSecUser = User.findByUsername(username)
        if (clubSecUser == null) {

            Role clubSecRole = Role.findByAuthority("ROLE_CLUB_SECRETARY")
            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            clubSecUser = new User(username: username, enabled: true, password: "password").save(flush: true)
            UserRole.create(clubSecUser, clubSecRole, true)
            UserRole.create(clubSecUser, readOnlyRole, true)
        }
        Club club = Club.findByName(clubName)
        if ((club != null) && (!club.hasSecretary(username))) {
            club.addToSecretaries(clubSecUser)
            club.save()
        }
    }

    private static void addReadOnlyUser(String username) {

        User readOnlyUser = User.findByUsername(username)
        if (readOnlyUser == null) {

            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            readOnlyUser = new User(username: username, enabled: true, password: "password").save(flush: true)
            UserRole.create(readOnlyUser, readOnlyRole, true)
        }
    }

    private static void addRoles() {

        Role role = Role.findByAuthority("ROLE_REGISTRATION_SECRETARY")
        if (role == null) {
            new Role(authority: "ROLE_REGISTRATION_SECRETARY").save(flush: true)
        }
        role = Role.findByAuthority("ROLE_CLUB_SECRETARY")
        if (role == null) {
            new Role(authority: "ROLE_CLUB_SECRETARY").save(flush: true)
        }
        role = Role.findByAuthority("ROLE_READ_ONLY")
        if (role == null) {
            new Role(authority: "ROLE_READ_ONLY").save(flush: true)
        }
    }
}
