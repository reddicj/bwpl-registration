package org.bwpl.registration.upload

import org.bwpl.registration.Club
import org.bwpl.registration.Competition
import org.bwpl.registration.Division
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.TestUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.web.multipart.MultipartFile

import static org.fest.assertions.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class RegistrationUploaderTest {

    @Before
    void setUp() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Club c2 = new Club(name: "Bedford", asaName: "Bedford")

        Team t1 = new Team(name: "Poly1", isMale: true)
        Team t2 = new Team(name: "Bedford Men", isMale: true)
        division.addToTeams(t1).addToTeams(t2)

        c1.addToTeams(t1).save()
        c2.addToTeams(t2).save()
    }

    @After
    void tearDown() {

    }

    @Test
    void testUploadGoodData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(goodData.getBytes("UTF-8"))

        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = TestUtils.mockSecurityUtils

        Team t = Team.findByName("Poly1")
        teamUploader.upload(t, f)
        assertThat(Registration.count).isEqualTo(2)
    }

    @Test
    void testUploadBadData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(badData.getBytes("UTF-8"))

        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = TestUtils.mockSecurityUtils

        Team t = Team.findByName("Poly1")
        try {
            teamUploader.upload(t, f)
            Assert.fail()
        }
        catch (UploadException e) {
            assertThat(e.message).contains("csv file errors")
        }
    }

    @Test
    void testUploadDuplicateTeamRegistrationsDiffRole() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(duplicateTeamRegistrationsDiffRole.getBytes("UTF-8"))

        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = TestUtils.mockSecurityUtils

        Team t = Team.findByName("Poly1")
        teamUploader.upload(t, f)
        assertThat(Registration.count).isEqualTo(2)
    }

    @Test
    void testUploadDuplicateTeamRegistrationsSameRole() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(duplicateTeamRegistrationsSameRole.getBytes("UTF-8"))

        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = TestUtils.mockSecurityUtils

        Team t = Team.findByName("Poly1")
        try {
            teamUploader.upload(t, f)
            Assert.fail()
        }
        catch (UploadException e) {
            assertThat(e.message).contains("is already registered")
        }
    }

    @Test
    void testUploadTeamAndRegistrationData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(teamAndRegistrationData.getBytes("UTF-8"))

        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = TestUtils.mockSecurityUtils

        teamUploader.upload(f)
        assertThat(Registration.count).isEqualTo(7)
        Registration registration = Registration.findByAsaNumber(17704)
        assertThat(registration.name).isEqualTo("Ziggy Tate")
        assertThat(registration.team.name).isEqualTo("Poly1")
        registration = Registration.findByAsaNumber(51)
        assertThat(registration.name).isEqualTo("Tony Bates")
        assertThat(registration.team.name).isEqualTo("Bedford Men")
    }

    private static final String goodData =
        "James,Reddick,666,player\n" +
        "Gary,Simons,123,COACH"

    private static final String badData =
        "James,Reddick,666 ,  Player\n" +
        "Gary,Simons , 123\n" +
        "Jerry,Birmingham , 456, Cleaner\n"

    private static final String duplicateTeamRegistrationsDiffRole =
        "James,Reddick,666,player\n" +
        "James,Reddick,666,COACH"

    private static final String duplicateTeamRegistrationsSameRole =
        "James,Reddick,666,player\n" +
        "James,Reddick,666,PLAYER"

    private static String teamAndRegistrationData =
        "Poly,Poly1,Elan,Stark,783421,Player\n" +
        "Poly,Poly1,Stuart,Stirling,52946,Player\n" +
        "Poly,Poly1,Ziggy,Tate,17704,Player\n" +
        "Poly,Poly1,Matt,White,9098,Player\n" +
        "Bedford,Bedford Men,Austin,Adkins,424,Player\n" +
        "Bedford,Bedford Men,Tony,Bates,51,Player\n" +
        "Bedford,Bedford Men,Adam,Boon,556074,Player"
}
