package neutra1.linter.rules;

import java.util.List;

import neutra1.linter.models.records.HeadingInfo;

public abstract class HeadingRule extends AbstractRule{

    protected final String ruleType = "Heading Rule";

    public HeadingRule(){
        super();
    }

    public HeadingInfo getHeadingInfoByText(List<String> texts, boolean ignoreCase){
        List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
        List<String> headingList = headingInfoList.stream().map(headingInfo -> headingInfo.text()).toList();
        if (ignoreCase){
            texts = texts.stream().map(text -> text.toLowerCase()).toList();
            headingList = headingList.stream().map(heading -> heading.toLowerCase()).toList();
        }
        boolean matchFound = false;
        int targetIndex = -1;
        for (String text : texts){
            for (int i = 0; i < headingList.size(); i++){
                if (headingList.get(i).equals(text)) {
                    matchFound = true;
                    targetIndex = i;
                    break;
                }
            }
            if (matchFound) {
                break;
            }
        }
        if (targetIndex == -1){
            return null;
        }
        return headingInfoList.get(targetIndex);
    }
}
