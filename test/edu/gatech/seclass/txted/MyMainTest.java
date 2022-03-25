package edu.gatech.seclass.txted;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyMainTest {

    // Place all  of your tests in this class, optionally using MainTest.java as an example.
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private final Charset charset = StandardCharsets.UTF_8;
    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file = createTmpFile();
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        fileWriter.write(input);
        fileWriter.close();
        return file;
    }

    private String getFileContent(String filename) {
        String content = null;
        try {
            content = Files.readString(Paths.get(filename), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*
     *   TEST CASES
     */

    // Frame #: 1
    @Test
    public void txtedTest1() throws Exception {
        String input = "";
        String expected = "";

        File inputFile = createInputFile(input);
        String[] args = {"-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 2
    @Test
    public void txtedTest2() throws Exception {
        String input = "01234abc";
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 3
    @Test
    public void txtedTest3() throws Exception {
        String[] args = new String[0];
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 4
    @Test
    public void txtedTest4() throws Exception {
        String input = "01234abc" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-f", "-f", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("File differs from expected", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 5
    @Test
    public void txtedTest5() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "dog", "-e", "abc", "-e", "ABC", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 6
    @Test
    public void txtedTest6() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-i", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 7
    @Test
    public void txtedTest7() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "0", "-s", "1", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 8
    @Test
    public void txtedTest8() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-x", "1", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 9
    @Test
    public void txtedTest9() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 10
    @Test
    public void txtedTest10() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01 01234abc" + System.lineSeparator() +
                "02 56789def" + System.lineSeparator() +
                "03 56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "2", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 11
    @Test
    public void txtedTest11() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01 01234abc" + System.lineSeparator() +
                "02 56789def" + System.lineSeparator() +
                "03 56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "1", "-n", "2", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 12
    @Test
    public void txtedTest12() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "-e", "abc", "i", "-s", "0", "-n", "2", "-x", "!", "-f", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("File differs from expected", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 13
    @Test
    public void txtedTest13() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 14
    @Test
    public void txtedTest14() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC\n", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 15
    @Test
    public void txtedTest15() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-x", "", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 16
    @Test
    public void txtedTest16() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-x", "ABC\n", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 17
    @Test
    public void txtedTest17() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 18
    @Test
    public void txtedTest18() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 19
    @Test
    public void txtedTest19() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "1", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 20
    @Test
    public void txtedTest20() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "2", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 21
    @Test
    public void txtedTest21() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 22
    @Test
    public void txtedTest22() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "3.2", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 23
    @Test
    public void txtedTest23() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = " 01234abc" + System.lineSeparator() +
                " 56789def" + System.lineSeparator() +
                " 56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 24
    @Test
    public void txtedTest24() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "-1", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 25
    @Test
    public void txtedTest25() throws Exception {
        String[] args = new String[0];
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 26
    @Test
    public void txtedTest26() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 27
    @Test
    public void txtedTest27() throws Exception {
        String input = "01234abc" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 28
    @Test
    public void txtedTest28() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator();
        String expected = "56789def!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 29
    @Test
    public void txtedTest29() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "", inputFile.getPath()};
        Main.main(args);
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: 30
    @Test
    public void txtedTest30() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234ABC" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 31
    @Test
    public void txtedTest31() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "01234ABC!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 32
    @Test
    public void txtedTest32() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234ABC!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-s", "0", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 33
    @Test
    public void txtedTest33() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 34
    @Test
    public void txtedTest34() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 35
    @Test
    public void txtedTest35() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 36
    @Test
    public void txtedTest36() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 37
    @Test
    public void txtedTest37() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 38
    @Test
    public void txtedTest38() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 39
    @Test
    public void txtedTest39() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 40
    @Test
    public void txtedTest40() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-s", "0", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 41
    @Test
    public void txtedTest41() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 42
    @Test
    public void txtedTest42() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF" + System.lineSeparator() +
                "56789def" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 43
    @Test
    public void txtedTest43() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 44
    @Test
    public void txtedTest44() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 45
    @Test
    public void txtedTest45() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 46
    @Test
    public void txtedTest46() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 47
    @Test
    public void txtedTest47() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 48
    @Test
    public void txtedTest48() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-e", "ABC", "-i", "-s", "0", "-x", "!", "-r", "-n", "2", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 49
    @Test
    public void txtedTest49() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 50
    @Test
    public void txtedTest50() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 51
    @Test
    public void txtedTest51() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 52
    @Test
    public void txtedTest52() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "01234ABC!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 53
    @Test
    public void txtedTest53() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 54
    @Test
    public void txtedTest54() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234ABC" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 55
    @Test
    public void txtedTest55() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "01234ABC!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 56
    @Test
    public void txtedTest56() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-s", "0", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 57
    @Test
    public void txtedTest57() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 58
    @Test
    public void txtedTest58() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 59
    @Test
    public void txtedTest59() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 60
    @Test
    public void txtedTest60() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator() +
                "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 61
    @Test
    public void txtedTest61() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 62
    @Test
    public void txtedTest62() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 63
    @Test
    public void txtedTest63() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 64
    @Test
    public void txtedTest64() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "01234abc!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-s", "0", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 65
    @Test
    public void txtedTest65() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 66
    @Test
    public void txtedTest66() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF" + System.lineSeparator() +
                "56789def" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 67
    @Test
    public void txtedTest67() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789def!" + System.lineSeparator() +
                "56789DEF!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 68
    @Test
    public void txtedTest68() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "56789DEF!" + System.lineSeparator() +
                "56789def!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 69
    @Test
    public void txtedTest69() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-s", "0", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 70
    @Test
    public void txtedTest70() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-s", "0", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 71
    @Test
    public void txtedTest71() throws Exception {
        String input = "" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-s", "0", "-x", "!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 72
    @Test
    public void txtedTest72() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "\n";

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-e", "ABC", "-i", "-s", "0", "-x", "!", "-r", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 73
    @Test
    public void txtedTest73() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
                "56789def" + System.lineSeparator() +
                "01234ABC" + System.lineSeparator() +
                "56789DEF" + System.lineSeparator();
        String expected = "0001 56789DEF!!!" + System.lineSeparator() + "" +
                "0002 01234ABC!!!" + System.lineSeparator() +
                "0003 56789def!!!" + System.lineSeparator() +
                "0004 01234abc!!!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "4", "-r", "-x", "!!!", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 74
    @Test
    public void txtedTest74() throws Exception {
        String input ="alphanumeric123foo" + System.lineSeparator() +
                "alphanumeric123Foo" + System.lineSeparator() +
                "alphanumeric123FOO" + System.lineSeparator() +
                "alphanumeric123bar" + System.lineSeparator() +
                "alphanumeric123Bar" + System.lineSeparator() +
                "alphanumeric123BAR" + System.lineSeparator() +
                "alphanumeric123foobar" + System.lineSeparator() +
                "alphanumeric123Foobar" + System.lineSeparator() +
                "alphanumeric123fooBar" + System.lineSeparator() +
                "alphanumeric123FooBar" + System.lineSeparator() +
                "alphanumeric123FOOBar" + System.lineSeparator() +
                "alphanumeric123FooBAR" + System.lineSeparator() +
                "alphanumeric123FOOBAR" + System.lineSeparator();
        String expected = "01 alphanumeric123FOOBAR!" + System.lineSeparator() +
                "02 alphanumeric123foobar!" + System.lineSeparator() +
                "03 alphanumeric123FOO!" + System.lineSeparator() +
                "04 alphanumeric123foo!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-n", "3", "-r", "-e", "Bar", "-s", "0", "-n", "2", "-x", "!", "-f", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("File differs from expected", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 75
    @Test
    public void txtedTest75() throws Exception {
        //no arguments on the command line will pass an array of length 0 to the application (not a null).
        String[] args = new String[0];
        Main.main(args);
        assertEquals("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

}
