package neutra1.tool.core;

import java.util.List;

import neutra1.tool.models.records.Violation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;

public class Reporter {
    
    private List<Violation> violationList;
    private static Reporter reporter = null;

    private Reporter() {
        this.violationList = new ArrayList<>();
    }

    public static Reporter getReporterInstance() {
        if (reporter == null) {
            reporter = new Reporter();
        }
        return reporter;
    }

    public void report(Violation violation) {
        violationList.add(violation);
    }

    public void outputDiagnostics() {
        StringBuilder diagnosis = getDiagnosis();
        System.out.println(diagnosis.toString());
    }

    public void outputDiagnostics(String outputFile, boolean override){
        StringBuilder diagnosis = getDiagnosis();
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path outputPath = Paths.get(outputFile);
        if (!outputPath.isAbsolute()){
            outputPath = currentDir.resolve(outputPath);
        }
        try{
            if (override){
                Files.writeString(outputPath, diagnosis.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }
            else {
                Files.writeString(outputPath, diagnosis.toString(), StandardOpenOption.CREATE);
            }
        }
        catch (IOException e){
            System.out.println("WARNING: writing to " + outputPath.toString() + " not successful." + "\n" +
                                "Output defaults to stdout. Please check path validity and/or write access.\n");
            outputDiagnostics();
        }
    }

    private StringBuilder getDiagnosis() {
        StringBuilder diagnosis = new StringBuilder();
        violationList.sort(Comparator.comparingInt(Violation::lineNumber));
        for (Violation v : violationList) {
            if (v.lineNumber() == -1) {
                diagnosis.append("[" + v.ruleId() + "] " + v.description() + "\n");
            }
            else {
                diagnosis.append("[" + v.ruleId() + "] Line " + v.lineNumber() + ": " + v.description() + "\n");
            }
        }
        return diagnosis;
    }
}
