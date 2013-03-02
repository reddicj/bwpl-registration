package org.bwpl.registration.upload

import org.junit.Test
import org.springframework.web.multipart.MultipartFile

import static org.fest.assertions.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CsvReaderTest {

    @Test
    void test() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(goodData.getBytes("UTF-8"))

        TestCsvHandler handler = new TestCsvHandler()
        CsvReader reader = new CsvReader(contentHandler: handler)
        reader.readFromMultipartFile(f)

        assertThat(handler.content.size()).isEqualTo(3)
        assertThat(handler.content.get(1)[0]).isEqualTo("Polytechnic")
        assertThat(handler.content.get(1)[1]).isEqualTo("Poly")
        assertThat(handler.content.get(1)[2]).isEqualTo("Poly Men")
        assertThat(handler.content.get(1)[3]).isEqualTo("M")
        assertThat(handler.content.get(1)[4]).isEqualTo("1")
    }

    private static final String goodData =
        "Polytechnic,Poly,Poly Men,M,1\n" +
        "Bristol,Bris,Bristol Men,M,1\n" +
        "Otter,Ott,Otter Lutra,F,2"

    private static class TestCsvHandler implements CsvHandler {

        Map<Integer, List<String>> content = new HashMap<Integer, List<String>>()

        void processTokens(int lineNumber, String[] tokens) {
            content.put(lineNumber, tokens.toList())
        }
    }
}
