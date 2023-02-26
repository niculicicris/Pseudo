package ro.niculici.pseudo.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Source {
    private String source;

    public Source(String fileName) {
       try {
           source = Files.readString(Path.of(fileName));
       } catch (IOException e) {
           source = null;
       }
    }

    public String getSource() {
        return source;
    }
}
