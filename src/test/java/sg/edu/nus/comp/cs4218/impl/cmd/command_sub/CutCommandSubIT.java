package sg.edu.nus.comp.cs4218.impl.cmd.command_sub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CutCommandSubIT {
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Positive test cases
    @Test
    void testCutCommandAndEchoAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-c", "2", "`echo " + testFile3.toFile().getPath() + "`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "." + System.lineSeparator() + "," + System.lineSeparator() +
                "1" + System.lineSeparator() + "1" + System.lineSeparator() +
                "2" + System.lineSeparator() + "5" + System.lineSeparator() +
                "0" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndSedAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndDiffAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndGrepAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndWcAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndCutAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndLsAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`ls " + TestFileUtils.TESTDATA_DIR + "t*1*`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "2" + System.lineSeparator() +
                "ì" + System.lineSeparator() + " " + System.lineSeparator() +
                "t" + System.lineSeparator() + "f" + System.lineSeparator() + "c" + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testCutCommandAndSortAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        fail();
    }

    @Test
    void testCutCommandAndFindAsSubCommandShouldEvaluateSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("cut", "-b", "4", "`find " + TestFileUtils.TESTDATA_DIR +
                " -name \"test*.csv\"`");
        CallCommand callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        callCommand.evaluate(ourTestStdin, ourTestStdout);
        String expectedResult = "," + System.lineSeparator() + "3" + System.lineSeparator() +
                " " + System.lineSeparator() + " " + System.lineSeparator() +
                " " + System.lineSeparator() + "," + System.lineSeparator() + "," + System.lineSeparator();
        assertEquals(expectedResult, ourTestStdout.toString());
    }
}
