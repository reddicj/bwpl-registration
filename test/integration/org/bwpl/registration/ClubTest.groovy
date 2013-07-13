package org.bwpl.registration

import org.bwpl.registration.validation.Status
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class ClubTest {

    @Test
    void test() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save(flush: true)

        Club club = new Club(name: "Poly", asaName: "Poly")
        Team mensTeam = new Team(name: "Poly Men", isMale: true)
        Team womensTeam = new Team(name: "Poly Women", isMale: false)
        club.addToTeams(mensTeam)
        club.addToTeams(womensTeam)
        division.addToTeams(mensTeam)
        division.addToTeams(womensTeam)
        club.save()

        club = Club.get(club.id)
        assertThat(club.teams.size()).isEqualTo(2)
        assertThat(club.teams.collect{it.name}).contains("Poly Men").contains("Poly Women")

        competition = Competition.get(competition.id)
        assertThat(competition.divisions.size()).isEqualTo(1)
        assertThat(competition.divisions.collect{it.name}).contains("Mens Div 1")
    }

    @Test
    void testGetRegistrations() {

        Competition competition1 = new Competition(name: "BWPL", urlName: "bwpl")
        Competition competition2 = new Competition(name: "LWPL", urlName: "lwpl")
        Division division1 = new Division(rank: 1, name: "BWPL Mens Div 1", isMale: true)
        Division division2 = new Division(rank: 1, name: "LWPL Mens Div 1", isMale: true)
        competition1.addToDivisions(division1).save()
        competition2.addToDivisions(division2).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "BWPL Poly1", isMale: true)
        Team t2 = new Team(name: "LWPL Poly1", isMale: true)
        division1.addToTeams(t1)
        division2.addToTeams(t2)
        c.addToTeams(t1).save()
        c.addToTeams(t2).save()

        Registration r1 = new Registration()
        r1.asaNumber = 123
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""
        r1.statusDate = new Date()

        Registration r2 = new Registration()
        r2.asaNumber = 456
        r2.firstName = "Gary"
        r2.lastName = "Simons"
        r2.role = "Player"
        r2.status = Status.INVALID
        r2.statusNote = ""
        r2.statusDate = new Date()

        Registration r3 = new Registration()
        r3.asaNumber = 123
        r3.firstName = "James"
        r3.lastName = "Reddick"
        r3.role = "Player"
        r3.status = Status.INVALID
        r3.statusNote = ""
        r3.statusDate = new Date()

        t1.addToRegistrations(r1)
        t1.addToRegistrations(r2)
        t1.save()
        t2.addToRegistrations(r3)
        t2.save()

        Set<Registration> registrations = c.getRegistrations(competition1)
        assertThat(registrations.size()).isEqualTo(2)
        assertThat(registrations.find{it.firstName == "James"}).isNotNull()
        assertThat(registrations.find{it.firstName == "Gary"}).isNotNull()

        registrations = c.getRegistrations(competition2)
        assertThat(registrations.size()).isEqualTo(1)
    }
}
