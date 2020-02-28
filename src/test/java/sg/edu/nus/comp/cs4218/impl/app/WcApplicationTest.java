package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class WcApplicationTest {
    private WcApplication wcApplication;
    private String[] defaultWcArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "11"+  System.lineSeparator() +
            "1 test 1 2" +  System.lineSeparator() + "5" + System.lineSeparator() + "+";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    void setUp() {
        wcApplication = new WcApplication();
        defaultWcArgs = Collections.singletonList("-cw").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cases with run().
     */
    // Error test cases
    @Test
    void testRunWithNullOutputStreamShouldThrowWcException() {
        Throwable thrown = assertThrows(WcException.class, () -> wcApplication.run(defaultWcArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), WcApplication.COMMAND + ": " + ERR_NULL_STREAMS);
    }

    // Positive test cases
    @Test
    void testRunWithMultipleFilesShouldRunSuccessfully() throws WcException {
        wcApplication.run(Arrays.asList("-c", testFile3.toFile().getPath()).toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = String.format(" %7d", 53) + " " + testFile3.toFile().getPath() + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testRunWithNoFilesShouldRunSuccessfully() throws WcException {
        wcApplication.run(Collections.singletonList("-wcl").toArray(new String[1]), ourTestStdin, ourTestStdout);
        String expectedResult = "";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    /**
     * Test cases with countFromFiles().
     */
    // Error test cases
    @Test
    void testCountFromFilesUsingASingleFileWithFileNotFoundInDirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                false, false, false,
                "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCountFromFilesUsingMultipleFilesWithAtLeastOneFileNotFoundInDirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                false, false, false,
                testFile3.toFile().toString(), "no-file.txt"
        ));
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCountFromFilesWithNullFileNameShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                false, false, false,
                ((String[]) null)
        ));
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    /*@Test
    void testCountFromFilesUsingASingleFileWithFileHasNoReadAccessShouldThrowException() {
        testFile1.toFile().setReadable(false);
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                true, false, false,
                testFile1.toFile().getPath()
        ));
        testFile1.toFile().setReadable(true);
        assertEquals(thrown.getMessage(), ERR_NO_PERM);
    }*/

    @Test
    void testCountFromFilesUsingASingleFileWithFilenameIsADirShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromFiles(
                true, false, false,
                TestFileUtils.TESTDATA_DIR
        ));
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }

    // Positive test cases
    @Test
    void testCountFromFilesWithNoFlagArgsAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithNoFlagArgsAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithNoFlagArgsAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, false,
                testFile2.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, false,
                testFile3.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithCaseIndependentArgOnlyAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithCaseIndependentArgOnlyAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile3.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithCaseIndependentArgOnlyAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, false, true,
                testFile1.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile1.toFile().toString(), testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, false,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, false, true,
                testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithReverseOrderAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                false, true, true,
                testFile1.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndASingleFileShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile1.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidSimilarFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile2.toFile().toString(), testFile2.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromFilesWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndAllValidDistinctFilesShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromFiles(
                true, true, true,
                testFile1.toFile().toString(), testFile2.toFile().toString(), testFile3.toFile().toString()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with countFromStdin().
     */
    // Error test cases
    @Test
    void testCountFromStdinWithNullInputStreamShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.countFromStdin(
                false, false, false, null
        ));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Single test cases
    @Test
    void testCountFromStdinWithEmptyInputStreamShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, false,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases
    @Test
    void testCountFromStdinWithNoFlagArgsAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d %7d", 4, 7, 18);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithFirstWordNumArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, false, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 18);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithReverseOrderArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, true, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 4);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithCaseIndependentArgOnlyAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, false, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d", 7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithFirstWordNumAndReverseOrderArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, true, false,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 4, 18);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithFirstWordNumAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, false, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 7, 18);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithReverseOrderAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                false, true, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d", 4, 7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCountFromStdinWithFirstWordNumAndReverseOrderAndCaseIndependentArgAndValidStandardInputShouldRunSuccessfully() throws Exception {
        String actualResult = wcApplication.countFromStdin(
                true, true, true,
                ourTestStdin
        );
        String expectedResult = String.format(" %7d %7d %7d", 4, 7, 18);
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test cases with getCountReport().
     */
    // Error test cases
    @Test
    void testGetCountReportWithNullInputStreamShouldThrowException() {
        Throwable thrown = assertThrows(Exception.class, () -> wcApplication.getCountReport(null));
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    // Positive test cases
}
