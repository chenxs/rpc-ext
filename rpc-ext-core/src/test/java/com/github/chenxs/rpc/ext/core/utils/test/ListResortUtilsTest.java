package com.github.chenxs.rpc.ext.core.utils.test;

import com.github.chenxs.rpc.ext.core.utils.ListResortUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 〈一句话功能简述〉<br>
 * Description: ListResortUtilsTest
 *
 * @author hillchen
 * @create 2019/8/31 13:48
 */
public class ListResortUtilsTest {
    @Test
    public void listResortTest(){
        List<Integer> list1 = Stream.of(0,1,2,3,4,5,6,7).collect(Collectors.toList());

        ListResortUtils.resortList(list1,6,2,true);
        StringBuilder sb1 = new StringBuilder();
        list1.forEach(e -> sb1.append(e));
        Assert.isTrue("01345627".equals(sb1.toString()));

        List<Integer> list2 = Stream.of(0,1,2,3,4,5,6,7).collect(Collectors.toList());

        ListResortUtils.resortList(list2,6,2,false);
        StringBuilder sb2 = new StringBuilder();
        list2.forEach(e -> sb2.append(e));
        Assert.isTrue("01623457".equals(sb2.toString()));
    }
}