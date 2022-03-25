package edu.gatech.seclass.txted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

public class TxtEd implements TxtEdInterface {
    private String filePath;
    private String excludeString;
    private boolean caseInsensitive;
    private boolean skipLines;
    private int lineToSkip;
    private boolean reverseFile;
    private String suffix;
    private boolean addLineNumber;
    private int padding;
    private boolean inplaceEdit;
    private ArrayList<String> lines;

    public TxtEd() {
        reset();
    }

    @Override
    public void reset() {
        this.filePath = null;
        this.excludeString = null;
        this.caseInsensitive = false;
        this.skipLines = false;
        this.lineToSkip = 0;
        this.reverseFile = false;
        this.suffix = null;
        this.addLineNumber = false;
        this.padding = 0;
        this.inplaceEdit = false;
        this.lines = new ArrayList<>();
    }

    @Override
    public void setFilepath(String filepath) {
        this.filePath = filepath;
    }

    @Override
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public void setStringToExclude(String excludeString) {
        this.excludeString = excludeString;
        ArrayList<String> temp = new ArrayList<>();
        if (caseInsensitive) {
            for (String line : lines) {
                if (!line.toLowerCase(Locale.ROOT).contains(excludeString.toLowerCase(Locale.ROOT))) {
                    temp.add(line);
                }
            }
        } else {
            for (String line : lines) {
                if (!line.contains(excludeString)) {
                    temp.add(line);
                }
            }
        }
        lines = temp;
    }

    @Override
    public void setSkipLines(boolean skipLines, int lineToSkip) {
        this.skipLines = skipLines;
        this.lineToSkip = lineToSkip;
        ArrayList<String> temp = new ArrayList<>();
        if (lineToSkip == 1) {
            for (int i = 0; i < lines.size(); i++) {
                if (i % 2 == 1) {
                    temp.add(lines.get(i));
                }
            }
        } else {
            for (int i = 0; i < lines.size(); i++) {
                if (i % 2 == 0) {
                    temp.add(lines.get(i));
                }
            }
        }
        lines = temp;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
        ArrayList<String> temp = new ArrayList<>();
        for (String line : lines) {
            temp.add(line + suffix);
        }
        lines = temp;
    }

    @Override
    public void setReverseFile(boolean reverseFile) {
        this.reverseFile = reverseFile;
        ArrayList<String> temp = new ArrayList<>();
        Collections.reverse(lines);
        temp.addAll(lines);
        lines = temp;
    }

    @Override
    public void setAddLineNumber(boolean addLineNumber, int padding) {
        this.addLineNumber = addLineNumber;
        this.padding = padding;
        ArrayList<String> temp = new ArrayList<>();
        if (padding == 0) {
            for (int i = 0; i < lines.size(); i++) {
                temp.add(" " + lines.get(i));
            }
        } else {
            String zero = "0";
            for (int i = 0; i < lines.size(); i++) {
                temp.add(zero.repeat(padding - 1) + (i + 1) + " " + lines.get(i));
            }
        }
        lines = temp;
    }

    @Override
    public void setInplaceEdit(boolean inplaceEdit) {
        this.inplaceEdit = inplaceEdit;
    }

    @Override
    public void txted() throws TxtEdException {
        if (filePath == null) throw new TxtEdException("No filename provided");
        if (filePath.isEmpty() || filePath.isBlank()) throw new TxtEdException("Invalid filename");

        String fileContent;
        try {
            fileContent = Files.readString(Paths.get(filePath), Charset.defaultCharset());
        } catch (InvalidPathException e) {
            throw new TxtEdException("cannot find path");
        } catch (FileNotFoundException e) {
            throw new TxtEdException("Cannot find file");
        } catch (IOException e) {
            throw new TxtEdException("Cannot read input file");
        }

        lines = new ArrayList<>(Arrays.asList(fileContent.split(System.getProperty("line.separator"))));
        int os = lines.size();

        if (caseInsensitive && excludeString == null) {
            throw new TxtEdException("Option i must be used with e flag");
        }

        if (skipLines) {
            if (lineToSkip != 0 && lineToSkip != 1) {
                throw new TxtEdException("Integer s must be 0 or 1");
            } else {
                setSkipLines(true, lineToSkip);
            }
        }

        if (excludeString != null) {
            if (excludeString.equals("")) {
                throw new TxtEdException("Argument e cannot be empty");
            }
            if (excludeString.contains("\n")) {
                throw new TxtEdException("Argument e cannot contain line separators");
            } else {
                setStringToExclude(excludeString);
            }
        }

        if (suffix != null) {
            if (suffix.contains("\n")) {
                throw new TxtEdException("Argument x cannot contain line separators");
            } else {
                setSuffix(suffix);
            }
        }

        if (reverseFile) {
            setReverseFile(true);
        }

        if (addLineNumber) {
            if (padding < 0) {
                throw new TxtEdException("Argument n must be equal to or greater than 0");
            } else {
                setAddLineNumber(true, padding);
            }
        }

        String listString;
        if (fileContent.length() == 0) {
            listString = "";
        } else {
            if (os == 0) {
                listString = "\n";
            } else {
                listString = String.join(System.lineSeparator(), lines);
                listString += System.lineSeparator();
            }
        }

        if (inplaceEdit) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer.write(listString);
            writer.close();
            writer.flush();
        } else {
            System.out.print(listString);
        }
    }
}