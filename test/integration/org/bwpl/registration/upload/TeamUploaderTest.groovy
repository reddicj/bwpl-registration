package org.bwpl.registration.upload

import org.bwpl.registration.Club
import org.bwpl.registration.Competition
import org.bwpl.registration.Division
import org.junit.Test
import org.springframework.web.multipart.MultipartFile

import static org.fest.assertions.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import org.junit.Assert

class TeamUploaderTest {

    @Test
    void testUploadGoodData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(goodData.getBytes("UTF-8"))

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl").save()
        TeamUploader teamUploader = new TeamUploader()
        teamUploader.upload(competition, f)

        assertThat(Club.count).isEqualTo(3)
        Club club = Club.findByAsaName("Poly")
        assertThat(club.name).isEqualTo("Polytechnic")
        assertThat(club.asaName).isEqualTo("Poly")
        assertThat(club.teams.asList()[0].name).isEqualTo("Poly Men")
        assertThat(club.teams.asList()[0].isMale).isTrue()
        assertThat(club.teams.asList()[0].division.rank).isEqualTo(1)
        assertThat(competition.divisions.size()).isEqualTo(2)
        assertThat(Division.findByIsMale(true).teams.size()).isEqualTo(2)
        assertThat(Division.findByIsMale(false).teams.size()).isEqualTo(1)
    }

    @Test
    void testUploadBadData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(badData.getBytes("UTF-8"))

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        TeamUploader teamUploader = new TeamUploader()
        try {
            teamUploader.upload(competition, f)
            Assert.fail()
        }
        catch (UploadException e) {
            String msg = e.message
            assertThat(e.message).contains("csv file errors")
        }
    }

    private static final String goodData =
        "Polytechnic,Poly,Poly Men,M,1\n" +
        "Bristol,Bris,Bristol Men,M,1\n" +
        "Otter,Ott,Otter Lutra,F,2"

    private static final String badData =
    """ "Bedford","Bedford","Bedford Men","M"
    "Bristol Central","Bristol Central","Bristol Central Men","M"
    "Bristol Central","","Bristol Central Women","F"
    "Caledonia","Caledonia Men","M"
    "Caledonia","Caledonia","Caledonia Women","W" """
}
