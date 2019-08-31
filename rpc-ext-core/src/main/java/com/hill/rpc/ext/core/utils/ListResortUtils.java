package com.hill.rpc.ext.core.utils;

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