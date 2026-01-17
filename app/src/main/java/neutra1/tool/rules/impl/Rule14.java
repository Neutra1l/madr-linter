package neutra1.tool.rules.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.tool.models.records.ImageInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.LinkRule;

public class Rule14 extends LinkRule{

    private final String RULE_ID_A = "MADR14a";
    private final String RULE_ID_B = "MADR14b";

    public Rule14(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 14;
    }

    @Override
    public void check(){
        Map<String, Integer> faultyImages = new HashMap<>();
        Map<String, Integer> absoluteMarkdownPaths = new HashMap<>();
        List<ImageInfo> imgInfos = traverser.getImageInfoList();
        for (ImageInfo imgInfo : imgInfos){
            String url = imgInfo.url();
            int startLineNumber = imgInfo.startLineNumber();
            if (isAbsolutePath(url)){
                faultyImages.put(url, startLineNumber);
            }
            else if (isExternalLink(url)){
                try {
                    int statusCode = establishHeadConnection(url);
                    if (statusCode < 200 || statusCode > 400){
                        faultyImages.put(url, startLineNumber);
                    }
                }
                catch (Exception e){
                    faultyImages.put(url, startLineNumber);
                }
            }
            else {
                if (url.startsWith("/")){
                    absoluteMarkdownPaths.put(url, startLineNumber);
                }
                else {
                    try {
                        boolean exists = pathExists(url);
                        if (!exists){
                            faultyImages.put(url, startLineNumber);
                        }
                    }
                    catch (Exception e){
                        faultyImages.put(url, startLineNumber);
                    }
                }
            }
        }
        StringBuilder descriptionA = new StringBuilder("The following image URLs are faulty:\n");
        StringBuilder descriptionB = new StringBuilder("Use of absolute path links in Markdown is discouraged:\n");
        buildDescription("", faultyImages, descriptionA);
        buildDescription("", absoluteMarkdownPaths, descriptionB);
        if (!faultyImages.isEmpty()){
            reporter.report(new Violation(RULE_ID_A, descriptionA.toString(), -1));
        }
        if (!absoluteMarkdownPaths.isEmpty()){
            reporter.report(new Violation(RULE_ID_B, descriptionB.toString(), -1));
        }
    } 
}
