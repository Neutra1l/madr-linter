package neutra1.linter.helper;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IgnoreFileHandler {

    private final List<PathMatcher> pathMatchers = new ArrayList<>();

    public IgnoreFileHandler(String workingDir){ 
        Path ignoreFile = Paths.get(workingDir).resolve(".madrlintignore");
        if  (Files.exists(ignoreFile)) {
            try {
                Files.readAllLines(ignoreFile).stream().map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(pattern -> {
                        String syntax = pattern.contains("*") ? "glob:" : "glob:**/";
                        pathMatchers.add(FileSystems.getDefault().getPathMatcher(syntax + pattern));
                    });
                } 
                catch (Exception e) {
                    System.out.println(".madrlintignore file unreadable. Using default settings.");
                }
            }   
    }

    public boolean isIgnored(Path path) {
        return pathMatchers.stream().anyMatch(m -> m.matches(path));
    }

    public boolean isIgnored(String path){
        return pathMatchers.stream().anyMatch(m -> m.matches(Paths.get(path)));
    }
}
