package cn.hill4j.rpcext.core.utils;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * Description: 列表重排工具类
 *
 * @author hillchen
 * @create 2019/8/31 13:34
 */
public class ListResortUtils {
    private ListResortUtils(){}

    /**
     * 将list中的两个元素互换位置
     *
     * @param list
     * @param before
     * @param after
     * @param afterFixed 后置元素固定标志:
     *                   如果为true,则将原列表中顺序较大的元素与其他元素之间的位置关系不变，
     *                   即将中间元素和原列表中顺序较大的元素整体前移,然后将原列表中顺序较小的元素插入到空位中
     *                   如果为true,则反正
     *                   示例：
     *         List<Integer> list1 = Stream.of(0,1,2,3,4,5,6,7).collect(Collectors.toList());
     *
     *         ListResortUtils.resortList(list1,6,2,true);
     *         StringBuilder sb1 = new StringBuilder();
     *         list1.forEach(e -> sb1.append(e));
     *         Assert.isTrue("01345627".equals(sb1.toString()));
     *
     *         List<Integer> list2 = Stream.of(0,1,2,3,4,5,6,7).collect(Collectors.toList());
     *
     *         ListResortUtils.resortList(list2,6,2,false);
     *         StringBuilder sb2 = new StringBuilder();
     *         list2.forEach(e -> sb2.append(e));
     *         Assert.isTrue("01623457".equals(sb2.toString()));
     *
     * @param <T>
     */
    public static <T> void resortList(List<T> list, T before, T after, boolean afterFixed){
        if (list == null){
            return;
        }
        int beforeIndex = list.indexOf(before);
        int afterIndex = list.indexOf(after);
        if (beforeIndex < 0 || afterIndex < 0 || beforeIndex < afterIndex){
            return;
        }
        if (afterFixed){
            for (int index = afterIndex+ 1; index <= beforeIndex ; index ++ ){
                list.set(index - 1,list.get(index));
            }
            list.set(beforeIndex ,after);
        }else {
            for (int index = beforeIndex -1; index >= afterIndex; index -- ){
                list.set(index + 1,list.get(index));
            }
            list.set(afterIndex ,before);
        }
    }
}