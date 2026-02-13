package neutra1.linter.rules.impl.global;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IGlobalRule;
import neutra1.linter.rules.NamingRule;

public class Rule05 extends NamingRule implements IGlobalRule {

    private final String RULE_ID_A = "MADR05a";
    private final String RULE_ID_B = "MADR05b";

    public Rule05(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 5;
    }

    @Override
    public void check(){
        if (validMadrNames.isEmpty()){
            return;
        }
        List<Integer> madrIds = validMadrNames.stream().map(
            pathString -> Integer.parseInt(Paths.get(pathString)
            .getFileName().toString().substring(0,4)))
            .sorted().toList();
        int smallestId = madrIds.get(0);
        if (smallestId >= 2){
            String formattedId = String.format("%04d", smallestId);
            String description = "Expected the smallest MADR Id in the folder to be either 0000 or 0001. Found " + formattedId;
            reporter.report(new Violation(RULE_ID_A, description, -1));
        }
        Path madrFolder = Paths.get(traverser.getInternalPath());
        List<DisconnectedMadrPair> disconnectedMadrPairList = new ArrayList<>();
        for (int i = 0; i < madrIds.size() - 1; i++){
            int diff = madrIds.get(i+1) - madrIds.get(i);
            if (diff > 1){
                String smallerMadr = madrFolder.relativize(Paths.get(validMadrNames.get(i))).toString();
                String biggerMadr = madrFolder.relativize(Paths.get(validMadrNames.get(i+1))).toString();
                String expectedMadr = String.format("%04d", madrIds.get(i) + 1) + "-*.md";
                disconnectedMadrPairList.add(new DisconnectedMadrPair(smallerMadr, biggerMadr, expectedMadr));
            }
        }
        if (!disconnectedMadrPairList.isEmpty()){
            StringBuilder description = new StringBuilder("Discontinuity in MADR Ids in folder " + madrFolder + ":\n");
            for (DisconnectedMadrPair disconnectedMadrPair : disconnectedMadrPairList){
                description.append(LISTING_INDENT_LONG + disconnectedMadrPair.smallerMadr() + 
                                   ": Expected " + disconnectedMadrPair.expectedMadr() + ". " + 
                                   "Found " + disconnectedMadrPair.biggerMadr() + "\n");
            }
            reporter.report(new Violation(RULE_ID_B, description.toString(), -1));
        }
    }

    private record DisconnectedMadrPair(String smallerMadr, String biggerMadr, String expectedMadr){
    }
}
