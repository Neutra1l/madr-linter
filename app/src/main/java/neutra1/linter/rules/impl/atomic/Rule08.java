package neutra1.linter.rules.impl.atomic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

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
            filter(linkInfo -> linkInfo.linkType() == LinkType.EXTERNAL).toList();
        final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL)
            .executor(Executors.newVirtualThreadPerTaskExecutor()).connectTimeout(Duration.ofSeconds(3)).build();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (LinkInfo link : externalLinkList) {
            String urlText = link.url();
            int lineNumber = link.startLineNumber();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlText))
                // Pretend to be a human on a Firefox browser sending this request on a Windows machine
                // See here: https://www.useragentstring.com/pages/Firefox/
                // and here: https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/User-Agent
                // in case you forget wtf this is
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0")
                .timeout(Duration.ofSeconds(3))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
            CompletableFuture<Void> future = client.sendAsync(request, HttpResponse.BodyHandlers.discarding()).
                thenAccept(response ->  {
                    int status = response.statusCode();
                    if (status < 200 || status > 400 && status != 403 && status != 429) {
                        invalidExternalLinks.put(urlText, lineNumber);
                    }   
                }).
                exceptionally(ex -> {
                    invalidExternalLinks.put(urlText, lineNumber);
                    return null;
                });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if (!invalidExternalLinks.isEmpty()){
            StringBuilder description = new StringBuilder("Non-reachable external links detected:\n");
            invalidExternalLinks.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .forEach(entry -> description.append(LISTING_INDENT_SHORT + "Line " + entry.getValue() + ": " + entry.getKey() + "\n"));
            reporter.report(new Violation(RULE_ID, description.toString(), -1));
        }
    }
}
