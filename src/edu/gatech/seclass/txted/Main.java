package edu.gatech.seclass.txted;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class Main {

    // Empty Main class for compiling Individual Project.
    // During Deliverable 1 and Deliverable 2, DO NOT ALTER THIS CLASS or implement it
    private static final Charset charset = StandardCharsets.UTF_8;
    private static CommandLine cmd = null;
    private static ArrayList<String> lines;
    private static String filecontent;

    public static void main(String[] args) throws FileNotFoundException {
        // Empty Skeleton Method

        // create command line parser
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        // build an option for option f
        Option f = Option.builder("f").hasArg(false).build();
        // add other options
        options.addOption("e", true, "remove lines containing string");
        options.addOption("i", false, "case insensitive match");
        options.addOption("s", true, "skip even or odd lines");
        options.addOption("x", true, "suffix string");
        options.addOption("r", false, "reverse order");
        options.addOption("n", true, "add line number and space");
        options.addOption(f);

        // throw parse exception to check format
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            usage();
            return;
        }

        // check the presence of arguments
        // last argument is file name
        ArrayList<String> l = new ArrayList<>(cmd.getArgList());
        if ((cmd.getOptions().length) == 0 && (l.size() > 1)) {
            usage();
            return;
        }
        if (l.size() == 0) {
            usage();
            return;
        }
        String file = l.get(l.size() - 1);

        //check file content requirements
        try {
            filecontent = new String(Files.readAllBytes(Paths.get(file)), charset);
        } catch (InvalidPathException e) {
            usage();
            return;
        } catch (FileNotFoundException e) {
            usage();
            return;
        } catch (IOException e) {
            usage();
            return;
        }

        // input file must be new line terminated
        //if (filecontent.substring(filecontent.length() - 1) != "\n") {
        //    usage();
        //    return;
        //}

        // break file content into string list by line breaks
        lines = new ArrayList<>(Arrays.asList(filecontent.split(System.getProperty("line.separator"))));
        int os = lines.size();

        // specifying option i without e should result in an error
        if (cmd.hasOption("i") && (!cmd.hasOption("e"))) {
            usage();
            return;
        }

        // presence of option s
        // integer s must be 0 or 1
        if (cmd.hasOption("s")) {
            String arg;
            arg = cmd.getOptionValues("s")[cmd.getOptionValues("s").length - 1];
            int s;
            try {
                s = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                usage();
                return;
            }
            if (s == 0 || s == 1) {
                lines = optionS(s);
            } else {
                usage();
                return;
            }
        }

        // presence of option e
        // empty string e results in an error
        if (cmd.hasOption("e")) {
            if (cmd.getOptionValues("e").length == 0 || cmd.getOptionValues("e") == null) {
                usage();
                return;
            } else {
                String arg;
                arg = cmd.getOptionValues("e")[cmd.getOptionValues("e").length - 1];
                if (arg.equals("")) {
                    usage();
                    return;
                }
                // string e must not contain newline
                if (arg.contains("\n")) {
                    usage();
                    return;
                } else {
                    lines = optionE(arg);
                }
            }
        }

        // presence of option x
        // string x must not be empty
        if (cmd.hasOption("x")) {
            if (cmd.getOptionValues("x").length == 0 || cmd.getOptionValues("x") == null) {
                usage();
                return;
            } else {
                String arg;
                arg = cmd.getOptionValues("x")[cmd.getOptionValues("x").length - 1];
                if (arg.equals("")) {
                    usage();
                    return;
                }
                // string e must not contain newline
                if (arg.contains("\n")) {
                    usage();
                    return;
                } else {
                    lines = optionX(arg);
                }
            }
        }

        // presence of option r
        if (cmd.hasOption("r")) {
            lines = optionR();
        }

        // presence of option n
        // integer n must be equal to or greater than 0
        if (cmd.hasOption("n")) {
            String arg;
            arg = cmd.getOptionValues("n")[cmd.getOptionValues("n").length - 1];
            int n;
            try {
                n = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                usage();
                return;
            }
            if (n < 0) {
                usage();
                return;
            } else {
                lines = optionN(n);
            }
        }

        // assemble output string
        // empty input content results in empty output
        String listString = new String();
        if (filecontent.length() == 0) {
            listString = "";
        } else {
            if (os == 0) {
                listString = "\n";
            } else {
                listString = String.join(System.lineSeparator(), lines);
                listString += System.lineSeparator();
            }
        }

        // presence of option f
        if (cmd.hasOption("f")) {
            PrintWriter writer = new PrintWriter(file);
            writer.write(listString);
            writer.close();
            writer.flush();
        } else {
            System.out.print(listString);
        }
    }

    // define method n
    private static ArrayList<String> optionN(int n) {
        ArrayList<String> temp = new ArrayList<>();
        if (n == 0) {
            for (int i = 0; i < lines.size(); i++) {
                temp.add(" " + lines.get(i));
            }
        } else {
            String zero = "0";
            for (int i = 0; i < lines.size(); i++) {
                temp.add(zero.repeat(n - 1) + (i + 1) + " " + lines.get(i));
            }
        }
        return temp;
    }

    // define method e
    private static ArrayList<String> optionE(String arg) {
        ArrayList<String> temp = new ArrayList<>();
        if (cmd.hasOption("i")) {
            for (String line : lines) {
                if (!line.toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))) {
                    temp.add(line);
                }
            }
        } else {
            for (String line : lines) {
                if (!line.contains(arg)) {
                    temp.add(line);
                }
            }
        }
        return temp;
    }

    // define method s
    private static ArrayList<String> optionS(int s) {
        ArrayList<String> temp = new ArrayList<>();
        if (s == 1) {
            for (int i = 0; i < lines.size(); i++) {
                if (i % 2 == 1) {
                    temp.add(lines.get(i));
                }
            }
        } else {
            for (int i = 0; i < lines.size(); i++ ) {
                if (i % 2 == 0) {
                    temp.add(lines.get(i));
                }
            }
        }
        return temp;
    }

    // define method x
    private static ArrayList<String> optionX(String arg) {
        ArrayList<String> temp = new ArrayList<>();
        for (String line : lines) {
            temp.add(line + arg);
        }
        return temp;
    }

    // define method r
    private static ArrayList<String> optionR() {
        ArrayList<String> temp = new ArrayList<>();
        Collections.reverse(lines);
        temp.addAll(lines);
        return temp;
    }

    private static void usage() {
        System.err.print("Usage: txted [ -f | -i | -s integer | -e string | -r | -x string | -n integer ] FILE");
    }
}
