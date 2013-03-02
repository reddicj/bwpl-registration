package org.bwpl.registration

import static org.fest.assertions.Assertions.assertThat
import static org.junit.Assert.*
import org.junit.*

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

        Club club = new Club(name: "Poly", asaName: "Poly").save()
        Team mensTeam = new Team(name: "Poly Men", isMale: true)
        Team womensTeam = new Team(name: "Poly Women", isMale: false)
        club.addToTeams(mensTeam)
        club.addToTeams(womensTeam)
        club.save()

        club = Club.get(club.id)
        assertThat(club.teams.size()).isEqualTo(2)
        assertThat(club.teams.collect{it.name}).contains("Poly Men").contains("Poly Women")
    }
}
