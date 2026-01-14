package neutra1.tool.rules.impl;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.InlineLinkInfo;
import neutra1.tool.rules.LinkRule;

public class Rule08 extends LinkRule{

    private final String RULE_ID = "MADR08";
    private final String DESCRIPTION_INDENT = "          ";
    private final String LISTING_INDENT = DESCRIPTION_INDENT + "    ";
    private List<String> invalidExternalLinkList;
    private List<String> invalidPathList;
    private List<String> invalidAnchorLinkList;
    private Map<String, Integer> linkLineNumberMap;

    public Rule08() {
        super();
        invalidExternalLinkList = new ArrayList<>();
        invalidPathList = new ArrayList<>();
        invalidAnchorLinkList = new ArrayList<>();
        linkLineNumberMap = new HashMap<>();
    }

    @Override
    public void check(){
        List<InlineLinkInfo> inlineLinkInfoList = traverser.getInlineLinkInfoList();
        for (InlineLinkInfo link : inlineLinkInfoList){
            String urlText = link.url();
            if (isExternalLink(urlText)){
                try{
                    URL url = URI.create(urlText).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    int status = conn.getResponseCode();
                    if (status < 200 || status > 300){
                        invalidExternalLinkList.add(urlText);
                    }
                }
                catch (IOException e){
                    invalidExternalLinkList.add(urlText);
                }
            }
            else if (isAnchorLink(urlText)){
                List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
                List<String> anchorRefIdList = headingInfoList.stream().map(headingInfo -> headingInfo.anchorRefId()).toList();
                boolean matches = anchorRefIdList.stream().anyMatch(anchorRefId -> anchorRefId.equals(urlText));
                if (!matches){
                    invalidAnchorLinkList.add(urlText);
                }
            }
            else if(isAbsolutePath(urlText)){
                try{
                    Path path = Paths.get(urlText);
                    if (!Files.exists(path)){
                        invalidPathList.add(urlText);
                    }
                }
                catch (Exception e){
                    invalidPathList.add(urlText);
                }
            }
            else {
                try{
                    Path madrPath = Paths.get(traverser.getMadrPath());
                    Path resolvedPath = madrPath.resolve(urlText).normalize();
                    if (!Files.exists(resolvedPath)){
                        invalidPathList.add(urlText);
                    }
                }
                catch (Exception e){
                    invalidPathList.add(urlText);
                }
            }
        }
        







        // List<LinkInfo> linkInfoList = traverser.getLinkInfoList();
        // for (LinkInfo linkInfo : linkInfoList){
        //     String linkUrl = linkInfo.url();
        //     // Anchor links are handled separately 
        //     if(linkUrl.startsWith("#")){
        //         linkLineNumberMap.put(linkUrl, linkInfo.startLineNumber());
        //         continue;
        //     }
        //     linkLineNumberMap.put(linkUrl, linkInfo.startLineNumber());
        //     URI uri;
        //     // This implementation below is stupid and hacky. Will need to find some other way later.
        //     try {
        //         uri = URI.create(linkUrl);
        //     } catch (IllegalArgumentException e){
        //         String linkUrlNormalized = linkUrl.replace("\\", "/").replace("file:///", "");
        //         Path path = Paths.get(linkUrlNormalized);
        //         uri = path.toUri();
        //     }
        //     String linkType = uri.getScheme();
        //     // http or https
        //     if (linkType.contains("http")){
        //         try {
        //         URL url = URI.create(linkUrl).toURL();
        //         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //         conn.setRequestMethod("HEAD");
        //         conn.setConnectTimeout(5000);
        //         conn.setReadTimeout(5000);
        //         int status = conn.getResponseCode();
        //         responseCodeMap.put(linkType, status);
        //         }
        //         catch (IOException e){
        //             invalidExternalLinkList.add(linkUrl);
        //         }
        //     } else if (linkType.equals("file")) {
        //         String linkUrlNormalized = linkUrl.replace("\\", "/").replace("file:///", "");
        //         Path path = Paths.get(linkUrlNormalized);
        //         if (!Files.exists(path)) {
        //             invalidDirectoryList.add(linkUrl);
        //         }
        //     } else {
        //         unsupportedLinkTypeList.add(linkUrl);
        //     }
        // }
        // checkForBadAnchorLinks();
        // boolean brokenLinksPresent = !invalidExternalLinkList.isEmpty() || !invalidExternalLinkList.isEmpty() ||
        //                              !unsupportedLinkTypeList.isEmpty() || !invalidDirectoryList.isEmpty();
        // if (brokenLinksPresent) {
        //     StringBuilder description = new StringBuilder("The following links in " + traverser.getMadrPath() + " are invalid:\n");
        //     description = buildDescription(invalidExternalLinkList, "External links", description);
        //     description = buildDescription(invalidAnchorLinkList, "Anchor links", description);
        //     description = buildDescription(invalidDirectoryList, "Local directories", description);
        //     description = buildDescription(unsupportedLinkTypeList, "Unsupported/Untested", description);
        //     reporter.report(new Violation(RULE_ID, description.toString(), -1));
        // }                         
    }

    private boolean isExternalLink(String url){
        try {
        URI uri = new URI(url);
        return uri.getScheme() != null &&
               (uri.getScheme().equalsIgnoreCase("http") ||
                uri.getScheme().equalsIgnoreCase("https"));
        } 
        catch (Exception e) {
            return false;
        }
    }

    private boolean isAnchorLink(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getFragment() != null && uri.getScheme() == null){
                 return true;
            } 
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean isAbsolutePath(String path){
        try {
            return Paths.get(path).isAbsolute();
        } 
        catch (Exception e) {
            return false;
        }
    }


    private StringBuilder buildDescription(List<String> brokenLinkList, String type, StringBuilder description){
        description.append(DESCRIPTION_INDENT + type + ":\n");
        brokenLinkList.stream().forEach(url -> description.append(LISTING_INDENT + "Line " + linkLineNumberMap.get(url).intValue() + ": " + url + "\n" ));
        return description;
    }
}
