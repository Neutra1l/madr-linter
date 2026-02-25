package neutra1.linter.rules.impl.atomic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.linter.models.enums.LinkType;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IAtomicRule;
import neutra1.linter.rules.LinkRule;

public class Rule08 extends LinkRule implements IAtomicRule {

    private final String RULE_ID = "MADR08"; 
    private HashMap<String, Integer> invalidExternalLinks;

    public Rule08() {
        super();
        invalidExternalLinks = new HashMap<>();
    }

    @Override
    public int getRuleNumber(){
        return 8;
    }

    @Override
    public void check(){
        List<LinkInfo> externalLinkList = traverser.getLinkInfoList().stream().
                                        filter(linkInfo -> linkInfo.linkType() == LinkType.EXTERNAL)
                                        .toList();
        for (LinkInfo link : externalLinkList){
            String urlText = link.url();
            int lineNumber = link.startLineNumber();
            try {
                int status = establishHeadConnection(urlText);
                if (status < 200 || status > 400 && status != 403 && status != 429){
                    invalidExternalLinks.put(urlText, lineNumber);
                }
            }
            catch (Exception e){
                invalidExternalLinks.put(urlText, lineNumber);
            }
        }
        if (!invalidExternalLinks.isEmpty()){
            StringBuilder description = new StringBuilder("Non-reachable external links detected:\n");
            invalidExternalLinks.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .forEach(entry -> description.append(LISTING_INDENT_SHORT + "Line " + entry.getValue() + ": " + entry.getKey() + "\n"));
            reporter.report(new Violation(RULE_ID, description.toString(), -1));
        }
    }
}
