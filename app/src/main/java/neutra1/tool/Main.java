package neutra1.tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Parameters(index = "0", description = "Path to MADR document.")
    private String madrFile;
    @Option(names = {"--out", "-o"}, description = "Output the diagnostics to a file. If that file does not exist, it will be created.")
    private String outputFile;
    @Option(names = {"--override"}, description = "If the given output file already exists, it will be overwritten.")
    private boolean override;
    @Option(names = {"-q", "--quiet"}, description = "Information not relevant to the lint results will be suppressed.")
    boolean quietMode;
    @Option(names = {"-n", "--no-warn"}, description = "Disable warnings for certain rules", split = ",")
    private Set<Integer> disabledRules = new HashSet<>();

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
        System.out.println("Linting on " + madrFile + "...");
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
        rules.stream().filter(rule -> !disabledRules.contains(rule.getRuleNumber())).forEach(rule -> rule.check());
        int disabledRuleCount = disabledRules.size();
        int totalRuleCount = rules.size();
        if (outputFile == null){  
            reporter.outputDiagnostics(disabledRuleCount, totalRuleCount, quietMode);
        } 
        else {
            reporter.outputDiagnostics(outputFile, disabledRuleCount, totalRuleCount, override, quietMode);
        }
        System.out.println("Done.");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] { "-h" };
        }
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private String readFile(String filePath) throws IOException{
        Path path = Paths.get(filePath);
        if (!path.isAbsolute()){
            path = currentDir.resolve(filePath);
        }
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return content;
    }
    
}

