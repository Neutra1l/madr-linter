package neutra1.tool.models.records;

import java.util.ArrayList;
import java.util.List;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterable;

public record BulletListInfo(BulletList list, int startLineNumber){

    public List<BulletListItem> getItems(){
    ReversiblePeekingIterable<Node> iterable = list.getChildren();
        List<BulletListItem> items = new ArrayList<>();
        iterable.forEach(node -> {
            if (node instanceof BulletListItem item){
                items.add(item);
            }
        });
        return items;
    }
    
}