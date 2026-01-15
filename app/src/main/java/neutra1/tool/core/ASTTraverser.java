package neutra1.tool.core;

import java.util.ArrayList;
import java.util.List;

import neutra1.tool.models.records.AutoLinkInfo;
import neutra1.tool.models.records.BulletListInfo;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.InlineLinkInfo;
import neutra1.tool.models.records.MetadataInfo;
import neutra1.tool.models.records.ParagraphInfo;

import com.github.sbaudoin.yamllint.LintProblem;
import com.github.sbaudoin.yamllint.Linter;
import com.github.sbaudoin.yamllint.YamlLintConfig;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterBlock;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;

import lombok.Getter;

@Getter
public class ASTTraverser {

    private List<String> output;
    private List<HeadingInfo> headingInfoList;
    private List<BulletListInfo> bulletListInfoList;
    private List<InlineLinkInfo> inlineLinkInfoList;
    private List<AutoLinkInfo> autoLinkInfoList;
    private List<MetadataInfo> metadataInfoList;
    private List<ParagraphInfo> paragraphInfoList;
    private NodeVisitor visitor;
    private static ASTTraverser astTraverser = null;
    private Node document;
    private String madrPath;

    private ASTTraverser() {
        this.output = new ArrayList<>();
        this.headingInfoList = new ArrayList<>();
        this.bulletListInfoList = new ArrayList<>();
        this.inlineLinkInfoList = new ArrayList<>();
        this.autoLinkInfoList = new ArrayList<>();
        this.metadataInfoList = new ArrayList<>();
        this.paragraphInfoList = new ArrayList<>();
        this.visitor = new NodeVisitor(
            new VisitHandler<>(Heading.class, this::visitHeading),
            new VisitHandler<>(Paragraph.class, this::visitParagraph),
            new VisitHandler<>(Link.class, this::visitLink),
            new VisitHandler<>(AutoLink.class, this::visitAutoLink),
            new VisitHandler<>(Reference.class, this::visitReference),
            new VisitHandler<>(YamlFrontMatterBlock.class, this::visitMetadata),
            new VisitHandler<>(BulletList.class, this::visitBulletList)
        );
    }

    private ASTTraverser(String madrPath) {
        this();
        this.madrPath = madrPath;
    }

    public static ASTTraverser getASTTraverserInstance() {
        if (astTraverser == null) {
            astTraverser = new ASTTraverser();
        }
        return astTraverser;
    }

    public static ASTTraverser getASTTTraverserInstance(String madrPath){
        if (astTraverser == null) {
            astTraverser = new ASTTraverser(madrPath);
        }
        else{
            setMadrPath(madrPath);
        }
        return astTraverser;
        
    }

    public void traverse(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.TRACK_DOCUMENT_LINES, true);
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true);
        options.set(Parser.EXTENSIONS, List.of(YamlFrontMatterExtension.create()));
        Parser parser = Parser.builder(options).build();
        this.document = parser.parse(markdown);
        visitor.visit(this.document);
    }

    private void visitHeading(Heading heading) {
        output.add("Heading level " + heading.getLevel() + ": " + heading.getText() + ". Line: " + heading.getStartLineNumber());
        String text = heading.getText().toString();
        String rawText = heading.getChars().toString();
        String anchorRefId = heading.getAnchorRefId();
        int level = heading.getLevel();
        int startLineNumber = heading.getStartLineNumber() + 1;
        String subsequenceTillEnd = heading.baseSubSequence(heading.getEndOfLine()).toString();
        headingInfoList.add(new HeadingInfo(text, rawText, anchorRefId, level, startLineNumber, subsequenceTillEnd, buildSection(heading)));
        visitor.visitChildren(heading);

    }

    private void visitParagraph(Paragraph paragraph) {
        output.add("Paragraph: " + paragraph.getChars());
        String text = paragraph.getChars().toString();
        int startLineNumber = paragraph.getStartLineNumber() + 1;

        paragraphInfoList.add(new ParagraphInfo(text, startLineNumber));
        visitor.visitChildren(paragraph);
    }

    private void visitBulletList(BulletList list){
        int startLineNumber = list.getStartLineNumber() + 1;
        bulletListInfoList.add(new BulletListInfo(list, startLineNumber));
        visitor.visitChildren(list);
    }

    private void visitReference(Reference reference) {
        output.add("Reference: " + reference.getTitle() + " -> " + reference.getUrl());
        visitor.visitChildren(reference);
    }

    private void visitLink(Link link){
        output.add("Link: " + link.getText() + " url: " + link.toString());
        String text = link.getText().toString();
        String url = link.getUrl().toString();
        int startLineNumber = link.getStartLineNumber() + 1;
        inlineLinkInfoList.add(new InlineLinkInfo(text, url, startLineNumber));
        visitor.visitChildren(link);
    }

    private void visitAutoLink(AutoLink autoLink){
        output.add("Auto link:" + autoLink.getUrl());
        String url = autoLink.getUrl().toString();
        int startLineNumber = autoLink.getStartLineNumber() + 1;

        autoLinkInfoList.add(new AutoLinkInfo(url, startLineNumber));
        visitor.visitChildren(autoLink);
    }

    private void visitMetadata(YamlFrontMatterBlock metadata){
        String content = metadata.getChars().toString();
        final String defaultLintConfig = 
            "---\r\n" + //
            "\r\n" + //
            "yaml-files:\r\n" + //
            "  - '.*\\.yaml'\r\n" + //
            "  - '.*\\.yml'\r\n" + //
            "  - '\\.yamllint'\r\n" + //
            "\r\n" + //
            "rules:\r\n" + //
            "  anchors: enable\r\n" + //
            "  braces: enable\r\n" + //
            "  brackets: enable\r\n" + //
            "  colons: enable\r\n" + //
            "  commas: enable\r\n" + //
            "  comments:\r\n" + //
            "    level: warning\r\n" + //
            "  comments-indentation:\r\n" + //
            "    level: warning\r\n" + //
            "  document-end: disable\r\n" + //
            "  document-start: disable\r\n" + //
            "  empty-lines: enable\r\n" + //
            "  empty-values: enable\r\n" + //
            "  float-values: disable\r\n" + //
            "  hyphens: enable\r\n" + //
            "  indentation: enable\r\n" + //
            "  key-duplicates: enable\r\n" + //
            "  key-ordering: disable\r\n" + //
            "  line-length: enable\r\n" + //
            "  new-line-at-end-of-file: disable\r\n" + //
            "  new-lines: disable\r\n" + //
            "  octal-values: disable\r\n" + //
            "  quoted-strings: disable\r\n" + //
            "  trailing-spaces: enable\r\n" + //
            "  truthy:\r\n" + //
            "    level: warning";
        try {
            List<LintProblem> problems = Linter.run(content, new YamlLintConfig(defaultLintConfig));
            int startLineNumber = metadata.getStartLineNumber();
            int endlineNumber = metadata.getEndLineNumber();
            metadataInfoList.add(new MetadataInfo(content, startLineNumber, endlineNumber, problems));
        }
        catch (Exception e){
            System.out.println("WARNING: parsing YAML front matter unsuccessful. Checks for rule 12 will not be run.\n");
        }
    }

    private List<Node> buildSection(Heading heading){
        List<Node> bodyNodes = new ArrayList<>();
        Node current = heading.getNext();
        while(current != null){
            if (current instanceof Heading){
                return bodyNodes;
            }
            bodyNodes.add(current);
            current = current.getNext();
        }
        return bodyNodes;
    }

    private static void setMadrPath(String madrPath) {
        astTraverser.madrPath = madrPath;
    }
}
