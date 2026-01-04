package neutra1.tool.rules;

import java.util.List;
import java.util.regex.Pattern;

import neutra1.tool.models.records.HeadingInfo;

public abstract class HeadingRule extends AbstractRule{

    protected final String ruleType = "Heading Rule";

    public HeadingRule(){
        super();
    }

    protected HeadingInfo getHeadingInfoByText(List<String> texts, boolean ignoreCase){
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

    protected String getSubsequenceTillNextSameLevelHeading(HeadingInfo beginning, HeadingInfo end){
        if (end == null){
            return beginning.subsequenceTillEnd();
        }
        String outerSubsequence = beginning.subsequenceTillEnd();
        String innerSubsequence = end.subsequenceTillEnd();
        String result = outerSubsequence.replaceFirst(Pattern.quote(innerSubsequence), "");
        return result;
    }

    protected HeadingInfo findNextSameLevelHeading(HeadingInfo start){
        int targetLevel = start.level();
        List<HeadingInfo> headingInfoListSameLevel = traverser.getHeadingInfoList().stream().filter
                                        (headingInfo -> headingInfo.level() == targetLevel).toList();
        for (int i = 0; i < headingInfoListSameLevel.size(); i++){
            HeadingInfo current = headingInfoListSameLevel.get(i);
            if (current.equals(start)){
                if (i == headingInfoListSameLevel.size() - 1){
                    return null;
                }
                else {
                    return headingInfoListSameLevel.get(i + 1);
                }
            }
        }
        return null;
    }


}
