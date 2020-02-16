package test.sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import test.sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class CutApplicationTest {
    private CutApplication cutApplication;
    private String[] defaultCutArgs;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        cutApplication = new CutApplication();
        defaultCutArgs = Arrays.asList("-c","8").toArray(new String[1]);
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    /**
     * Test cut application with run().
     */

    @Test
    void testRunNullArgs() {
       Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(null, ourTestStdin, ourTestStdout));
       assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ERR_NULL_ARGS);
    }

    @Test
    void testRunNullOutputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(defaultCutArgs, ourTestStdin, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NO_OSTREAM);
    }

    @Test
    void testRunSuccess() throws CutException {
        //Use a sample test file.
        cutApplication.run(Arrays.asList("-c", "6", "README.md").toArray(new String[3]), ourTestStdin, ourTestStdout);
        assertEquals("2\na\nr\n", ourTestStdout.toString());
    }

    /**
     * Test cut application using cutFromFiles()
     */
    // Erronorous Test cases (13 cases)
    @Test
    void testCutFromFilesWithoutAnyPosFlags() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                false, false, false, 1, 5, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_MISSING_ARG);
    }

    @Test
    void testCutFromFilesUsingByteAndCharPos() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(
                true, true, false, 1, 5, testFile1.toFile().getPath()
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_TOO_MANY_ARGS);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithStartNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithEndNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndASingleFileWithStartNumIsNotAInteger() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndASingleFileWithEndNumIsNotAInteger() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndASingleFileWithNumIsNotAInteger() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                true, false, false, (int) 3.141, (int) 3.141,
                ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_INVALID_ARGS);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndASingleFileWithFileNotFoundInDir() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndMultipleFilesWithAtLeastOneFileNotFoundInDir() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndASingleFileWithInvalidFilename() {
        fail();
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndMultipleFilesWithAtLeastOneInvalidFilename() {
        fail();
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndNullFilename() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromFiles(false, false, false, 1, 2, (String[]) null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_GENERAL);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndEmptyFilenameString() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 15, 15,
                ""
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive test cases (50 cases)
    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 6, 13, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 13, 6, testFile1.toFile().getPath()
        );
        String expectedResult = "8w\n" + "šü\n" + "eo\n" + "cf\n" + "rp\n" + "al";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 6, 12, testFile1.toFile().getPath()
        );
        String expectedResult = "8: Soft\n" + "š mödü\n" + "egressi\n" + "cause o\n" + "rmance \n" + "al skil";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 12, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 5, 5, testFile1.toFile().getPath()
        );
        String expectedResult = "1\n" + "š\n" + "r\n" + "-\n" + "o\n" + "i";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 2, 29, testFile1.toFile().getPath()
        );
        String expectedResult = "S\n" + "hp\n" + "no\n" + "oi\n" + "eo\n" + "rd";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 56, 18, testFile1.toFile().getPath()
        );
        String expectedResult = "T \n" + "se\n" + "si\n" + "o \n" + "co\n" + " n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 3, 63, testFile1.toFile().getPath()
        );
        String expectedResult = "4218: Software Testing\n" +
                "ìš mödülè cövèrs thè concepts and prãctīće of software testin\n" +
                "d regression testing. Various testing coverage criteria will \n" +
                "ot-cause of errors in failing test cases will also be investi\n" +
                "rformance prediction, performance clustering and performance \n" +
                "ucial skills on testing and debugging through hands-on assign\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSingleFileWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 18, 17, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSingleFile() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 103, 103, testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "e\n" + "d\n" + "f\n" + "l\n" + "\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 10, 12,
                testFile1.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "ot\n" + "öü\n" + "si\n" + "eo\n" + "c \n" + "kl\n" + "u \n" +
                "\n" + "us\n" + "\n" + "sa";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 115, 1,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 1, 11,
                testFile1.toFile().getPath(), testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "CS4218: Sof\n" + "Thìš möd\n" + "and regress\n" + "root-cause \n" + "performance\n" + "crucial ski\n" +
                "Lorem ipsum\n" + "\n" + "Euismod qui\n" + "\n" + "Turpis mass\n" +
                "1.0, 5.0\n" + "2, 3\n" + "51, 15\n" + "21, 4\n" + "22, 41\n" + "551, 1200\n" + "001, 010";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 1,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 9, 9,
                testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "\n" + "e\n" + "c\n" +
                " \n" + "i\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 274,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 274, 1,
                testFile2.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "Ls\n" + "\n" + "Ed\n" + "\n" + "Tn\n" +
                "1\n" + "2\n" + "5\n" + "2\n" + "2\n" + "5\n" + "0";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 1, 200,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquam malesuada bibendum arcu vitae. Nam libero justo laoreet sit amet cur\n" +
                "\n" +
                "Euismod quis viverra nibh cras pulvinar mattis nunc. Nam libero justo laoreet sit amet cursus sit amet dictum. Auctor augue mauris augue neque gravida in fermentum et. Nunc eget lorem dolor sed viverr\n" +
                "\n" +
                "Turpis massa tincidunt dui ut ornare lectus sit. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Est velit egestas dui id ornare arcu odio ut sem. Facilisi nullam vehi\n" +
                "CS4218: Software Testing\n" +
                "Thìš mödülè cövèrs thè concepts and prãctīće of software testing including unït testing, integration testing,\n" +
                "and regression testing. Various testing coverage criteria will be discussed. Debugging methods for finding the\n" +
                "root-cause of errors in failing test cases will also be investigated. The use öf testing and analysis for\n" +
                "performance prediction, performance clustering and performance debugging will be studied. Students will acquire\n" +
                "crucial skills on testing and debugging through hands-on assignments.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidDistinctFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 200, 1,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidDistinctFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 250, 250,
                testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "t\n" + "\n" + "a\n" + "\n" + "i\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 3, 8,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndFileIsDashWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 8, 7,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 2, 17,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 12, 10,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndFileIsDash() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 15, 15,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 16, 18,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndFileIsDashWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 143, 18,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 56, 118,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndFileIsDashWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 156, 128,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndFileIsDash() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 20, 20,
                "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 32, 48,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "si\n" + "\n" + "pn\n" + "\n" +
                "r.\n" + "si\n" + "\n" + "pn\n" + "\n" + "r.";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 120, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath()
        );
        String expectedResult = "pq\n" + "\n" + " u\n" + "\n" + "mr\n" +
                "pq\n" + "\n" + " u\n" + "\n" + "mr";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 2, 18,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on \n" +
                "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on \n" +
                "S4218: Software T\n" + "hìš mödülè c\n" + "nd regression tes\n" + "oot-cause of erro\n" + "erformance predic\n" + "rucial skills on ";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 10, 8,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndValidSimilarFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 3, 3,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "0\n" + " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" +
                "0\n" + " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" +
                " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" + " \n" +
                ",\n" + ",\n" + ",\n" + "1\n" + "1\n" + "0\n" +
                " \n" + ",\n" + ",\n" + ",\n" + "1\n" + "1";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 1, 4,
                testFile3.toFile().getPath(), testFile3.toFile().getPath(), testFile3.toFile().getPath(),
                testFile3.toFile().getPath()
        );
        String expectedResult = "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,\n" +
                "1,\n" + "23\n" + "5 \n" + "2 \n" + "2 \n" + "5,\n" + "0,";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 6, 3,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua\n" +
                "48\n" + "ìm\n" + "de\n" + "oc\n" + "rr\n" + "ua";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 6, 17,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on\n" +
                "8: Software \n" + "mödülè cövèr\n" + "egression te\n" + "cause of err\n" + "rmance predi\n" + "al skills on";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndValidSimilarFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 16, 4,
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath(), testFile1.toFile().getPath(), testFile1.toFile().getPath(),
                testFile1.toFile().getPath()
        );
        String expectedResult = "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
                "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndValidSimilarFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 8, 8,
                testFile2.toFile().getPath(), testFile2.toFile().getPath(), testFile2.toFile().getPath(),
                testFile2.toFile().getPath()
        );
        String expectedResult = "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m\n" +
                "p\n" + "\n" + " \n" + "\n" + "m\n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 9, 18,
                "-", testFile2.toFile().getPath(), "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 18, 9,
                "-", testFile1.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 10, 19,
                testFile3.toFile().getPath(), testFile1.toFile().getPath(), testFile3.toFile().getPath(),
                "-", "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, true, 20, 19,
                "-", "-", testFile3.toFile().getPath(), testFile1.toFile().getPath(),
                testFile3.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingBytePosAndSingleNumAndHavingDashBetweenMultipleFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                false, true, false, 22, 22,
                "-", testFile3.toFile().getPath(), "-", testFile1.toFile().getPath(),
                testFile3.toFile().getPath(), "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 2, 13,
                "-", testFile2.toFile().getPath(), "-", testFile1.toFile().getPath(),
                testFile3.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAnd2CommaSeparatedNumAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 15, 3,
                "-", testFile2.toFile().getPath(), testFile1.toFile().getPath(),
                "-", "-", testFile1.toFile().getPath(), testFile3.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 7, 23,
                "-", testFile2.toFile().getPath(), testFile1.toFile().getPath(), "-",
                "-", "-", testFile1.toFile().getPath(), testFile3.toFile().getPath(), "-", "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndNumRangeAndHavingDashBetweenMultipleFilesWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, true, 15, 6,
                "-", testFile1.toFile().getPath(), testFile3.toFile().getPath(), "-"
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromFilesUsingCharPosAndSingleNumAndHavingDashBetweenMultipleFiles() throws Exception {
        String actualResult = cutApplication.cutFromFiles(
                true, false, false, 15, 15,
                testFile3.toFile().getPath(), testFile1.toFile().getPath(),
                "-", "-", testFile2.toFile().getPath(), testFile1.toFile().getPath()
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }


    /**
     * Test cut application using cutFromStdin()
     */
    // Erronorous Test cases (9 cases)
    @Test
    void testCutFromStdinWithoutAnyPosFlags() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                false, false, false, 1, 5, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_MISSING_ARG);
    }

    @Test
    void testCutFromStdinUsingByteAndCharPos() {
        Throwable thrown =
                assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                true, true, false, 1, 5, ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_TOO_MANY_ARGS);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumIsNotAInteger() {
        fail();
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithEndNumIsNotAInteger() {
        fail();
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumLessThanZero() {
        fail();
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithNumIsNotAInteger() throws Exception {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(
                true, false, false, (int) 5.1, (int) 5.1,
                ourTestStdin
        ));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_INVALID_ARGS);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndNullInputStream() {
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.cutFromStdin(false, false, false, 1, 2, null));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": "  + ERR_NULL_STREAMS);
    }

    // Single Test cases (1 case)
    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStreamWithAnEmptyInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 4, 4,
                new ByteArrayInputStream(new byte[0])
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    // Positive Test cases (10 cases)
    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 1, 200,
                ourTestStdin
        );
        String expectedResult = "d";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 200, 1,
                ourTestStdin
        );
        String expectedResult = "d";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 2, 56,
                ourTestStdin
        );
        String expectedResult = "rüberspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, true, 61, 33,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingBytePosAndSingleNumAndValidInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                false, true, false, 5, 5,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 15,
                ourTestStdin
        );
        String expectedResult = "n";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAnd2CommaSeparatedNumAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 14, 1,
                ourTestStdin
        );
        String expectedResult = "dn";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumLowerThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 4, 14,
                ourTestStdin
        );
        String expectedResult = "berspringen";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndNumRangeAndValidInputStreamWithStartNumHigherThanEndNum() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 13, 9,
                ourTestStdin
        );
        String expectedResult = "";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCutFromStdinUsingCharPosAndSingleNumAndValidInputStream() throws Exception {
        String actualResult = cutApplication.cutFromStdin(
                true, false, false, 4, 4,
                ourTestStdin
        );
        String expectedResult = "b";
        assertEquals(expectedResult, actualResult);
    }
}