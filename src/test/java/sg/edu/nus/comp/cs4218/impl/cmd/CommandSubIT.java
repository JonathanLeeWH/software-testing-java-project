package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import sg.edu.nus.comp.cs4218.impl.app.EchoApplication;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.TestFileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class CommandSubIT {
    private ArgumentResolver argumentResolver;
    private EchoApplication echoApplication;
    private InputStream ourTestStdin;
    private OutputStream ourTestStdout;
    private static final String TEST_STDIN_MSG_1 = "drüberspringen";
    private final Path testFile1 = Paths.get(TestFileUtils.TESTDATA_DIR + "test1.txt");
    private final Path testFile2 = Paths.get(TestFileUtils.TESTDATA_DIR + "test2.txt");
    private final Path testFile3 = Paths.get(TestFileUtils.TESTDATA_DIR + "test3.csv");

    @BeforeEach
    public void setUp() {
        argumentResolver = new ArgumentResolver();
        echoApplication = new EchoApplication();
        ourTestStdin = new ByteArrayInputStream(TEST_STDIN_MSG_1.getBytes());
        ourTestStdout = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        ourTestStdin.close();
        ourTestStdout.close();
    }

    // Error test cases
    @Test
    void testWcCommandAndCutAsSubCommandWithErrorExceptionThrownShouldThrowCutException() {
        List<String> args = Arrays.asList("wc", "`cut -x 300 "+testFile1.toFile().getPath()+"`");
        Throwable thrown = assertThrows(CutException.class, () -> argumentResolver.parseArguments(args));
        assertEquals(thrown.getMessage(), CutApplication.COMMAND + ": " + ArgsParser.ILLEGAL_FLAG_MSG + "x");
    }

    // Positive test cases
    @Test
    void testLsCommandAndEchoAsSubCommandWithBlankOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`cut -c 200 "+testFile1.toFile().getPath()+"`");
        List<String> expectedResult = Arrays.asList("echo");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testLsCommandAndEchoAsSubCommandWithOneLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "-d", "`echo "+testFile1.toFile().getPath()+"`", "-R");
        List<String> expectedResult = Arrays.asList("ls", "-d", testFile1.toFile().getPath(), "-R");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testWcCommandAndCutAsSubCommandWithMultipleLineOutputShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "`echo " + testFile1.toFile().getPath()  + " " + testFile2.toFile().getPath() + "`");
        List<String> expectedResult = Arrays.asList("wc", testFile1.toFile().getPath(), testFile2.toFile().getPath());
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }

    @Test
    void testEchoCommandAndEchoAsSubCommandWithADoubleQuoteShouldParseArgumentsAndRunSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo \"Welcome to CS4218: Software Testing\"`");
        List<String> expectedArgsResult = Arrays.asList("echo", "Welcome", "to", "CS4218:", "Software" , "Testing");
        assertEquals(expectedArgsResult, argumentResolver.parseArguments(args));
        
        echoApplication.run(expectedArgsResult.toArray(new String[2]), ourTestStdin, ourTestStdout);
        String expectedResult = "echo Welcome to CS4218: Software Testing";
        assertEquals(expectedResult, ourTestStdout.toString());
    }

    @Test
    void testWcCommandAndLsAndGrepAndCutAsSubCommandUsingMultiplePipeOperatorShouldParseArgumentsSuccessfully() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("wc", "-c", "`ls | grep s* | cut -c 1-3`");
        List<String> expectedResult = Arrays.asList("wc", "-c", "src");
        assertEquals(expectedResult, argumentResolver.parseArguments(args));
    }
}
