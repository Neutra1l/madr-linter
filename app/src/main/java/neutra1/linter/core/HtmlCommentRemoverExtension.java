package neutra1.linter.core;

import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.ast.HtmlInlineComment;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.Parser.ParserExtension;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;

public class HtmlCommentRemoverExtension implements ParserExtension {

    static class NodeRemoverPostProcessor extends NodePostProcessor {
        private static class NodeRemoverFactory extends NodePostProcessorFactory {
            NodeRemoverFactory(DataHolder options) {
                super(false);
                addNodes(HtmlCommentBlock.class);
                addNodes(HtmlInlineComment.class);
            }
            @NotNull
            @Override
            public NodePostProcessor apply(@NotNull Document document) {
                return new NodeRemoverPostProcessor();
            }
        }

        public static NodePostProcessorFactory Factory(DataHolder options) {
            return new NodeRemoverFactory(options);
        }

        @Override
        public void process(@NotNull NodeTracker state, @NotNull Node node) {
            if (node instanceof HtmlInlineComment || node instanceof HtmlCommentBlock) {
                node.unlink();
                state.nodeRemoved(node);
            } 
        }
    }
    
    private HtmlCommentRemoverExtension() { }

    @Override
    public void parserOptions(MutableDataHolder options) { }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(NodeRemoverPostProcessor.Factory(parserBuilder));
    }

    public static HtmlCommentRemoverExtension create() {
        return new HtmlCommentRemoverExtension();
    }
}
    

