package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasteApplicationTest {
    private static final String FILE_TYPE = ".txt";
    private static final String EMPTY_FILE = "testFileZero";
    private static final String TWO_LINES_FILE = "testFileOne";
    private static final String ONE_LINE_FILE = "testFileTwo";
    private static final String EMPTY = "";
    private static final String TWO_LINES = "First Line" + System.lineSeparator() + "Second Line";
    private static final String ONE_LINE = "Only One Line";
    private static File emptyFile;
    private static File twoLinesFile;
    private static File oneLineFile;
    private static PasteApplication pasteApplication;
    private static OutputStream outputStreamZero, outputStreamOne, outputStreamTwo;
    private static final String FIRST_LINE = "First Line";
    private static final String SECOND_LINE = "Second Line";

    @BeforeAll
    public static void setupBeforeTest() throws IOException {
        pasteApplication = new PasteApplication();
        try {
            emptyFile = File.createTempFile(EMPTY_FILE, FILE_TYPE);
            twoLinesFile = File.createTempFile(TWO_LINES_FILE, FILE_TYPE);
            oneLineFile = File.createTempFile(ONE_LINE_FILE, FILE_TYPE);
            outputStreamZero = new FileOutputStream(emptyFile);
            outputStreamOne = new FileOutputStream(twoLinesFile);
            outputStreamTwo = new FileOutputStream(oneLineFile);
            outputStreamZero.write(EMPTY.getBytes()   );
            outputStreamOne.write(TWO_LINES.getBytes());
            outputStreamTwo.write(ONE_LINE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDownAfterTesting() {
        emptyFile.deleteOnExit();
        twoLinesFile.deleteOnExit();
        oneLineFile.deleteOnExit();
        try {
            outputStreamZero.close();
            outputStreamOne.close();
            outputStreamTwo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFile method when filename is invalid.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void runInvalidFileShouldThrowFileNotFoundException() {
        String invalidFile = "invalidTest";
        String[] args = { invalidFile };
        assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
    }

    /**
     *  Test run when dash is not in the first argument.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void runDashInNonFirstArgumentShouldThrowPasteException() {
        String[] args = {"ok.txt", "-"};
        try (InputStream inputStream = new FileInputStream(twoLinesFile)) {
            assertThrows(PasteException.class, () -> {
                pasteApplication.run(args, inputStream, outputStreamOne);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFile method when filename is empty.
     *  Expected: Throws FileNotFound Exception
     */
    @Test
    public void runEmptyFileNameShouldThrowFileNotFoundException() {
        String[] args = { oneLineFile.toPath().toString(), ""};
        assertThrows(FileNotFoundException.class, () -> {
            pasteApplication.mergeFile(args);
        });
    }

    /**
     * Test mergeFile method when file content is empty.
     *  Expected: Print Nothing.
     */
    @Test
    public void runOneEmptyFileContentShouldPrintNothing() throws Exception {
        String[] fileNames = new String[1];
        fileNames[0] = emptyFile.toPath().toString();
        assertEquals(System.lineSeparator(), pasteApplication.mergeFile(fileNames));
    }

    /**
     * Test mergeFile method when filename is the name of a file with two lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void runTwoLinesFileShouldPrintTwoLines() throws Exception {
        String[] fileName = new String[1];
        fileName[0] = twoLinesFile.toPath().toString();
        assertEquals(TWO_LINES + System.lineSeparator(), pasteApplication.mergeFile(fileName));
    }

    /**
     * Test mergeFile method when two filenames are given.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    public void runMergeMultipleFilesShouldMergeAllFilesAndPrintMergedContents() throws Exception {
        String tab = "\t";
        String[] args = { twoLinesFile.toPath().toString(), oneLineFile.toPath().toString() };
        String expectedOutput = FIRST_LINE + tab + "Only One Line" + System.lineSeparator()
                + "Second Line" + System.lineSeparator();
        String actualOutput = pasteApplication.mergeFile(args);
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void runStdinSingleLineShouldPrintSingleLine() throws Exception {
        try(InputStream inputStream = new FileInputStream(oneLineFile)) {
            assertEquals(ONE_LINE + System.lineSeparator(), pasteApplication.mergeStdin(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void runStdinSingleLineWithDashShouldPrintSingleLine() throws Exception {
        try(InputStream inputStream = new ByteArrayInputStream(oneLineFile.toPath().toString().getBytes())) {
            String[] args = {"-"};
            pasteApplication.run(args, inputStream, outputStreamTwo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has multiple lines.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void runStdinMultipleLinesOnMergeStdinMethodShouldPrintMultipleLines() throws Exception {
        try(InputStream inputStream = new FileInputStream(twoLinesFile)) {
            assertEquals(TWO_LINES + System.lineSeparator(), pasteApplication.mergeStdin(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeStdin method when no filenames are given and Stdin contains only one file that has a single line.
     *  Expected: Returns a string of the file contents and terminates with a newline.
     */
    @Test
    public void runStdinMultiLineWithDashShouldPrintMergedContents() throws Exception {
        String input = oneLineFile.toPath().toString() + " " + oneLineFile.toPath().toString();
        try(InputStream inputStream = new ByteArrayInputStream(input.getBytes())) {
            String[] args = {"-"};
            pasteApplication.run(args, inputStream, outputStreamTwo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test mergeFileAndStdin method when one filename is given and Stdin contains only one file.
     *  Expected: Returns a string with the two file contents merged (tab-concatenated).
     */
    @Test
    public void runMergeStdinAndSingleFileShouldMergeAllFilesAndPrintMergedContents() throws Exception {
        String tab = "\t";
        String input = twoLinesFile.toPath().toString();
        try (InputStream inputStream = new ByteArrayInputStream(input.getBytes())) {
            String[] fileNames = { twoLinesFile.toPath().toString() };
            String expectedOutput = FIRST_LINE + tab + FIRST_LINE + System.lineSeparator()
                    + SECOND_LINE + tab + SECOND_LINE + System.lineSeparator();
            assertEquals(expectedOutput, pasteApplication.mergeFileAndStdin(inputStream, fileNames));
        }
    }
}
