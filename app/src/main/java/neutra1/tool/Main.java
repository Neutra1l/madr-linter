package neutra1.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jspecify.annotations.NonNull;

import neutra1.tool.core.ASTTraverser;
import neutra1.tool.core.Reporter;
import neutra1.tool.rules.AbstractRule;
import neutra1.tool.rules.impl.Rule01;
import neutra1.tool.rules.impl.Rule02;
import neutra1.tool.rules.impl.Rule03;
import neutra1.tool.rules.impl.Rule04;
import neutra1.tool.rules.impl.Rule06;
import neutra1.tool.rules.impl.Rule07;
import neutra1.tool.rules.impl.Rule08;
import neutra1.tool.rules.impl.Rule09;
import neutra1.tool.rules.impl.Rule10;
import neutra1.tool.rules.impl.Rule11;
import neutra1.tool.rules.impl.Rule12;
import neutra1.tool.rules.impl.Rule13;
import neutra1.tool.rules.impl.Rule14;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "madrlint",
    description = "Lint MADR files",
    mixinStandardHelpOptions = true,
    version="1.0.0"
)
public class Main implements Runnable {

    private final String RESET = "\u001B[0m";
    private final String RED   = "\u001B[31m";
    private final Path currentDir = Paths.get(System.getProperty("user.dir"));
    @Parameters(index = "0", description = "File path to lint")
    private String madrFile;
    @Option(names = {"--out", "-o"}, description = "Output the diagnostics to a file. If that file does not exist, it will be created.")
    private String outputFile;
    @Option(names = {"--override"}, description = "If the given output file already exists, it will be overwritten.")
    private boolean override;
    @Override
    public void run() {
        madrFile = currentDir.resolve(madrFile).toString();
        ASTTraverser astTraverser = ASTTraverser.getASTTTraverserInstance(madrFile);
        Reporter reporter = Reporter.getReporterInstance();
        try {
            astTraverser.traverse(readFile(madrFile));
        }
        catch (IOException ioException){
            System.out.println(RED + "Error: unable to read input file " + madrFile);
            System.exit(2);
        }
        // astTraverser.getOutput().toString().lines().forEach(System.out::println);
        List<@NonNull AbstractRule> rules = List.of(
            new Rule01(),
            new Rule02(),
            new Rule03(),
            new Rule04(),
            new Rule06(),
            new Rule07(),
            new Rule08(),
            new Rule09(),
            new Rule10(),
            new Rule11(),
            new Rule12(),
            new Rule13(),
            new Rule14()
        );
        for (AbstractRule rule : rules) {
            rule.check();
        }
        if (outputFile == null){  
            reporter.outputDiagnostics();
        } 
        else {
            reporter.outputDiagnostics(outputFile, override);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private String readFile(String filePath) throws IOException{
        Path path = Paths.get(filePath);
        if (!path.isAbsolute()){
            path = currentDir.resolve(filePath);
        }
        String content = new String(Files.readAllBytes(path));
        return content;
    }
    
}

