package cn.hill4j.rpcext.core.utils;

import java.util.List;

/**
 * 2019/8/31 13:34 <br>
 * Description: 列表重排工具类
 *
 * @author hillchen
 */
public class ListResortUtils {
    private ListResortUtils(){}

    /**
     * 将list中的两个元素互换位置
     *
     * @param list 重排集合
     * @param before 需要前置的对象
     * @param after 需要后置置的对象
     * @param afterFixed 后置元素固定标志:
     *                   如果为true,则将原列表中顺序较大的元素与其他元素之间的位置关系不变，
     *                   即将中间元素和原列表中顺序较大的元素整体前移,然后将原列表中顺序较小的元素插入到空位中
     *                   如果为true,则反正
     *                   示例一：
     *                   原列表为:{0,1,2,3,4,5,6,7}
     *                   入参为：{before:6,after:2,afterFixed:true}
     *                   重排后列表{0,1,3,4,5,6,2,7}
     *                   示例二：
     *                   原列表为:{0,1,2,3,4,5,6,7}
     *                   入参为：{before:6,after:2,afterFixed:false}
     *                   重排后列表{0,1,6,2,3,4,5,7}
     *
     * @param <T> 重排对象类型
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
