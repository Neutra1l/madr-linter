package org.tool;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.tool.core.ASTTraverser;
import org.tool.core.Reporter;
import org.tool.rules.AbstractRule;
import org.tool.rules.impl.Rule01;
import org.tool.rules.impl.Rule02;
import org.tool.rules.impl.Rule03;
import org.tool.rules.impl.Rule04;
import org.tool.rules.impl.Rule06;
import org.tool.rules.impl.Rule08;
import org.tool.rules.impl.Rule09;
import org.tool.rules.impl.Rule10;
import org.tool.rules.impl.Rule11;
import org.tool.rules.impl.Rule12;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "madrlint",
    description = "Prints a simple greeting",
    mixinStandardHelpOptions = true
)
public class App implements Runnable {

    @Parameters(
        index = "0",
        description = "File path to lint"
    )
    private String filePath;
    @Override
    public void run() {
        ASTTraverser astTraverser = ASTTraverser.getASTTTraverserInstance(filePath);
        Reporter reporter = Reporter.getReporterInstance();
        astTraverser.traverse(readFile(filePath));
        astTraverser.getOutput().toString().lines().forEach(System.out::println);
        List<@NonNull AbstractRule> rules = List.of(
            new Rule01(),
            new Rule02(),
            new Rule03(),
            new Rule04(),
            new Rule06(),
            new Rule08(),
            new Rule09(),
            new Rule10(),
            new Rule11(),
            new Rule12()
        );
        for (AbstractRule rule : rules) {
            rule.check();
        }
        reporter.outputDiagnostics();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
    public static String readFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
    }
    
}
}
