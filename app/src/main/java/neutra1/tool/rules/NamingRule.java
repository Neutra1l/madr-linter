package neutra1.tool.rules;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class NamingRule extends AbstractRule{
    
    protected final String ruleType = "Naming Rule";
    private final String MADR_FILE_NAMING_REGEX = "^\\d{4}-.+\\.md$";
    protected List<String> nonMarkdownFiles;
    protected List<String> madrsWithNamingViolations;
    protected List<String> validMadrNames;

    public NamingRule(){
        super();
        nonMarkdownFiles = new ArrayList<>();
        madrsWithNamingViolations = new ArrayList<>();
        validMadrNames = new ArrayList<>();
        classifyFilesInMadrFolder();
    }

    private List<Path> getAllFilesInMadrFolder(){
        String madrPath = traverser.getMadrPath();
        Path parentFolder = Path.of(madrPath).getParent();
        DirectoryStream<Path> directoryStream = null;
        List<Path> paths = null;
        try {
            directoryStream = Files.newDirectoryStream(parentFolder);
            paths = new ArrayList<>();
            for (Path path : directoryStream){
                paths.add(path);
            }
        }
        catch(IOException e){
            System.err.println("Error reading directory: " + madrPath + "\n." + 
            "Check for adherence to MADR naming conventions was not performed.");
        }
        finally{
            if (directoryStream != null){
                try {
                    directoryStream.close();
                } catch (IOException e) {
                    System.out.println("Error closing directory stream: " + e.getMessage());
                }
            }
        }
        return paths;
    }

    private void classifyFilesInMadrFolder(){
        List<Path> paths = getAllFilesInMadrFolder();
        if (paths == null){
            return;
        }
        Pattern pattern = Pattern.compile(MADR_FILE_NAMING_REGEX);
        for (Path filePath : paths) {
            String fileName = filePath.getFileName().toString();
            String absolutePath = filePath.toString();
            if (!fileName.contains(".md")) {
                this.nonMarkdownFiles.add(absolutePath);
            }
            else if (!pattern.matcher(fileName).matches()) {
                this.madrsWithNamingViolations.add(absolutePath);
            }
            else {
                this.validMadrNames.add(absolutePath);
            }
        }
    }
}
