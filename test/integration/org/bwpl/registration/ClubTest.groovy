package org.bwpl.registration

import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class ClubTest {

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void test() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save(flush: true)

        Club club = new Club(name: "Poly", asaName: "Poly")
        competition.addToClubs(club).save(flush: true)

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
}
