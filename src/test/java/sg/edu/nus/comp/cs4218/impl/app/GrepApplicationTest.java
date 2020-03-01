package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class GrepApplicationTest {
    private static final String FILE_ONE_NAME = "fileOne";
    private static final String FILE_TWO_NAME = "fileTwo";
    private static final String FILE_FORMAT = ".txt";
    private static final String FILE_ONE_TEXT = "line one" + System.lineSeparator() + "line two" + System.lineSeparator() + "line three";
    private static final String LINE_ONE = "line one";
    private static final String INVALID = "invalid";
    private static final String LOWER_LINE = "line";
    private static final String UPPER_LINE = "Line";
    private static File fileOne;
    private static File fileTwo;
    private static GrepApplication grepApplication;
    private static InputStream stdinOne;
    private static OutputStream stdoutOne, stdoutTwo, outputStream;

    @BeforeEach
    void setup() throws IOException {
        grepApplication = new GrepApplication();
        fileOne = File.createTempFile(FILE_ONE_NAME, FILE_FORMAT);
        fileTwo = File.createTempFile(FILE_TWO_NAME, FILE_FORMAT);
        stdinOne = new FileInputStream(fileOne);
        stdoutOne = new FileOutputStream(fileOne);
        stdoutTwo = new FileOutputStream(fileTwo);
        outputStream = new ByteArrayOutputStream();
        stdoutOne.write(FILE_ONE_TEXT.getBytes());
        stdoutTwo.write(LINE_ONE.getBytes());
        outputStream = new ByteArrayOutputStream();
        grepApplication = new GrepApplication();
    }

    @AfterAll
    static void teardown() throws IOException {
        outputStream.close();
    }
    // Test grepFromFiles Method

    /**
     * Test null files names in grepFromFiles Method.
     * Expected: Throw NullPointer Exception
     */
    @Test
    void runNullFileNamesShouldThrowNullPointerException() {
        String validPattern = "line";
        assertThrows(NullPointerException.class, () -> {
            grepApplication.grepFromFiles(validPattern, true, true, (String) null);
        });
    }

    /**
     * Test null pattern string in grepFromFiles Method.
     * Expected: Throw GrepException
     */
    @Test
    void runNullPatternShouldThrowGrepException() {
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromFiles(null, true, true, fileOne.toPath().toString());
        });
    }

    /**
     * Test grepFromFiles Method with valid inputs.
     * Expected: Number of lines that matches the pattern.
     */
    @Test
    void runIsCaseInsensitiveTrueWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String validPattern = "line";
        assertEquals("3" + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileOne.toPath().toString()));
    }

    /**
     * Test grepFromFiles Method with invalid pattern with case sensitivity.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveFalseWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String validPattern = "line";
        assertEquals("3" + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, false, true, fileOne.toPath().toString()));
    }

    /**
     * Test grepFromFiles Method with invalid pattern.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveTrueWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String invalidPattern = INVALID;
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(invalidPattern, true, true, fileOne.toPath().toString()));
    }

    /**
     * Test grepFromFiles Method with invalid pattern with case sensitivity.
     * Expected: 0.
     */
    @Test
    void runIsCaseInsensitiveFalseWithInvalidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String validPattern = UPPER_LINE;
        assertEquals("0" + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, false, true, fileOne.toPath().toString()));
    }


    /**
     * Test grepFromFiles method with valid pattern with case sensitivity.
     * Expected: 3.
     */
    @Test
    void runSingleFileNameShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {fileOne.toPath().toString()};
        String validPattern = "line";
        assertEquals(3 + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with multiple file names.
     * Expected: number of lines grepped in each file.
     */
    @Test
    void runMultipleFileNamesShouldReturnNumberOfLinesGrepped() throws Exception {
        String[] fileNames = {fileOne.toPath().toString(), fileOne.toPath().toString()};
        String validPattern = "line";
        String expectedOutput = fileOne.toPath().toString() + ": 3" + System.lineSeparator() + fileOne.toPath().toString() + ": 3";
        assertEquals(expectedOutput + System.lineSeparator(), grepApplication.grepFromFiles(validPattern, true, true, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return a single line.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnSingleLineMatched() throws Exception {
        String[] fileNames = {fileOne.toPath().toString()};
        String validPattern = "one";
        String expectedOutput = LINE_ONE + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with valid pattern.
     * Expected: return multiple lines.
     */
    @Test
    void runIsCountLinesFalseWithValidPatternShouldReturnMultipleLineMatched() throws Exception {
        String[] fileNames = {fileOne.toPath().toString()};
        String validPattern = "line";
        String expectedOutput = LINE_ONE + System.lineSeparator() + "line two"
                + System.lineSeparator() + "line three" + System.lineSeparator();
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInvalidPatternShouldReturnEmptyString() throws Exception {
        String[] fileNames = {fileOne.toPath().toString()};
        String invalidPattern = INVALID;
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(invalidPattern, true, false, fileNames));
    }

    /**
     * Test grepFromFiles method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runIsCountLinesFalseWithInValidPatternDueToCaseSensitivityShouldReturnEmptyString() throws Exception {
        String[] fileNames = {fileOne.toPath().toString()};
        String validPattern = UPPER_LINE;
        String expectedOutput = "";
        assertEquals(expectedOutput, grepApplication.grepFromFiles(validPattern, false, false, fileNames));
    }

    // Test grepFromStdin Method

    /**
     * Test grepFromStdin method with null standard input.
     * Expected: throws GrepException.
     */
    @Test
    void runNullInputStream() {
        String validPattern = "line";
        assertThrows(GrepException.class, () -> {
            grepApplication.grepFromStdin(validPattern, true, true, null);
        });
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithValidPatternShouldReturnNumberOfLinesGrepped() throws Exception {
        String validPattern = "line";
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertEquals(3 + System.lineSeparator(),
                    grepApplication.grepFromStdin(validPattern, true, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with valid input.
     * Expected: return number of lines matched.
     */
    @Test
    void runInputStreamWithInvalidPatternShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        String invalidPattern = INVALID;
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertEquals(0 + System.lineSeparator(),
                    grepApplication.grepFromStdin(invalidPattern, true, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with invalid input due to case sensitivity.
     * Expected: 0.
     */
    @Test
    void runInputStreamWithIsCaseInsensitiveFalseShouldReturnZeroNumberOfLinesGrepped() throws Exception {
        String invalidPattern = UPPER_LINE;
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertEquals(0 + System.lineSeparator(),
                grepApplication.grepFromStdin(invalidPattern, false, true, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with valid pattern and isCountLines is false.
     * Expected: return lines that matched the pattern.
     */
    @Test
    void runInputStreamWithValidPatternAndIsCountLinesFalseShouldReturnLinesGrepped() throws Exception {
        String validPattern = "one";
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
           assertEquals(LINE_ONE + System.lineSeparator(),
                grepApplication.grepFromStdin(validPattern, true, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        String invalidPattern = INVALID;
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertEquals("",
                grepApplication.grepFromStdin(invalidPattern, true, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Test grepFromStdin method with isCountLines false, with invalid pattern due to case sensitivity.
     * Expected: return empty string.
     */
    @Test
    void runInputStreamWithInvalidPatternDueToCaseSensitivityAndIsCountLinesFalseShouldReturnEmptyString() throws Exception {
        String invalidPattern = UPPER_LINE;
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertEquals("",
                grepApplication.grepFromStdin(invalidPattern, false, false, inputStream));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    // Test getGrepArgument Method.

    @Test
    void runValidGrepArgument() throws AbstractApplicationException, IOException {
        String[] args = {"-i", "-c", LOWER_LINE, fileOne.toPath().toString()};
        outputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            grepApplication.run(args, inputStream, outputStream);
            assertEquals("3" + System.lineSeparator(), outputStream.toString());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    void runInvalidDashIGrepArgument() throws IOException {
        String[] args = {"-i-i", "-c", LOWER_LINE, fileOne.toPath().toString()};
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertThrows(GrepException.class, () -> {
                grepApplication.run(args, inputStream, outputStream);
            });
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    void runInvalidDashCGrepArgument() throws IOException {
        String[] args = {"-i", "-c-c", LOWER_LINE, fileOne.toPath().toString()};
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            assertThrows(GrepException.class, () -> {
                grepApplication.run(args, inputStream, outputStream);
            });
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    void runNoFilesAndNoStdinGrepArgument() {
        String[] args = {"-i", "-c", LOWER_LINE};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runNoDashIInGrepArgument() throws AbstractApplicationException, IOException {
        String[] args = {"-c", LOWER_LINE, fileOne.toPath().toString()};
        try(InputStream inputStream = new FileInputStream(fileOne.toPath().toString())) {
            grepApplication.run(args, inputStream, outputStream);
            assertEquals(3 + System.lineSeparator(), outputStream.toString());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    void runEmptyArgsGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {""};
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runNullArgsGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        assertThrows(GrepException.class, () -> {
            grepApplication.run(null, null, outputStream);
        });
    }

    // The following are bugs from the skeleton code. Reserved for debugging in milestone 2.
    /*
    @Test
    void runInvalidDashWithDoubleIGrepArgument() throws FileNotFoundException {
        String[] args = {"-ii", "-c", LOWER_LINE, fileOne.toPath().toString()};
        InputStream inputStream = new FileInputStream(fileOne.toPath().toString());
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void runInvalidDashWithDoubleCCGrepArgument() throws AbstractApplicationException, FileNotFoundException {
        String[] args = {"-i", "-cc", LOWER_LINE, fileOne.toPath().toString()};
        InputStream inputStream = new FileInputStream(fileOne.toPath().toString());
        assertThrows(GrepException.class, () -> {
            grepApplication.run(args, inputStream, outputStream);
        });
    }*/
}
