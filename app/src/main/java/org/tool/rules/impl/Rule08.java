package org.tool.rules.impl;

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

import org.tool.models.records.LinkInfo;
import org.tool.models.records.Violation;
import org.tool.rules.LinkRule;

public class Rule08 extends LinkRule{

    private final String RULE_ID = "MADR08";
    private final String DESCRIPTION_INDENT = "          ";
    private final String LISTING_INDENT = DESCRIPTION_INDENT + "    ";
    private List<String> invalidExternalLinkList;
    private List<String> unsupportedLinkTypeList;
    private List<String> invalidDirectoryList;
    private List<String> invalidAnchorLinkList;
    private Map<String, Integer> responseCodeMap;
    private Map<String, Integer> linkLineNumberMap;

    public Rule08() {
        super();
        invalidExternalLinkList = new ArrayList<>();
        unsupportedLinkTypeList = new ArrayList<>();
        invalidDirectoryList = new ArrayList<>();
        invalidAnchorLinkList = new ArrayList<>();
        responseCodeMap = new HashMap<>();
        linkLineNumberMap = new HashMap<>();
    }

    @Override
    public void check(){
        List<LinkInfo> linkInfoList = traverser.getLinkInfoList();
        for (LinkInfo linkInfo : linkInfoList){
            String linkUrl = linkInfo.url();
            // Anchor links are handled separately 
            if(linkUrl.startsWith("#")){
                linkLineNumberMap.put(linkUrl, linkInfo.startLineNumber());
                continue;
            }
            linkLineNumberMap.put(linkUrl, linkInfo.startLineNumber());
            URI uri;
            // This implementation below is stupid and hacky. Will need to find some other way later.
            try {
                uri = URI.create(linkUrl);
            } catch (IllegalArgumentException e){
                String linkUrlNormalized = linkUrl.replace("\\", "/").replace("file:///", "");
                Path path = Paths.get(linkUrlNormalized);
                uri = path.toUri();
            }
            String linkType = uri.getScheme();
            // http or https
            if (linkType.contains("http")){
                try {
                URL url = URI.create(linkUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int status = conn.getResponseCode();
                responseCodeMap.put(linkType, status);
                }
                catch (IOException e){
                    invalidExternalLinkList.add(linkUrl);
                }
            } else if (linkType.equals("file")) {
                String linkUrlNormalized = linkUrl.replace("\\", "/").replace("file:///", "");
                Path path = Paths.get(linkUrlNormalized);
                if (!Files.exists(path)) {
                    invalidDirectoryList.add(linkUrl);
                }
            } else {
                unsupportedLinkTypeList.add(linkUrl);
            }
        }
        checkForBadAnchorLinks();
        boolean brokenLinksPresent = !invalidExternalLinkList.isEmpty() || !invalidExternalLinkList.isEmpty() ||
                                     !unsupportedLinkTypeList.isEmpty() || !invalidDirectoryList.isEmpty();
        if (brokenLinksPresent) {
            StringBuilder description = new StringBuilder("The following links in " + traverser.getMadrPath() + " are invalid:\n");
            description = buildDescription(invalidExternalLinkList, "External links", description);
            description = buildDescription(invalidAnchorLinkList, "Anchor links", description);
            description = buildDescription(invalidDirectoryList, "Local directories", description);
            description = buildDescription(unsupportedLinkTypeList, "Unsupported/Untested", description);
            reporter.report(new Violation(RULE_ID, description.toString(), -1));
        }                         
    }

    private StringBuilder buildDescription(List<String> brokenLinkList, String type, StringBuilder description){
        description.append(DESCRIPTION_INDENT + type + ":\n");
        brokenLinkList.stream().forEach(url -> description.append(LISTING_INDENT + "Line " + linkLineNumberMap.get(url).intValue() + ": " + url + "\n" ));
        return description;
    }

    private void checkForBadAnchorLinks(){
        List<String> headingAnchorRefIdList = traverser.getHeadingInfoList()
        .stream().map(headingInfo -> headingInfo.anchorRefId()).toList();
        List<LinkInfo> anchorLinkList = traverser.getLinkInfoList()
        .stream().filter(linkInfo -> linkInfo.url().startsWith("#")).toList();
        List<String> anchorLinkUrlList = anchorLinkList.stream().map(link -> link.url()).toList();
        List<String> anchorLinkUrlListNoHashtag = anchorLinkUrlList.stream().map(url -> url.replace("#", "")).toList();
        boolean matchFound = false;
        for (String url : anchorLinkUrlListNoHashtag){
            matchFound = headingAnchorRefIdList.stream().anyMatch(refId -> refId.equals(url));
            if (!matchFound){
                invalidAnchorLinkList.add("#" + url);
            }
            matchFound = false;
        }   
    }
}
