package com.github.chenxs.rpc.ext.core.utils.test.annotation;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 〈一句话功能简述〉<br>
 * Description: ListSortTest
 *
 * @author hillchen
 * @create 2019/8/30 00:47
 */
public class ListSortTest {
    @Test
    public void resetListTest(){
        List<String> list = Stream.of("1","2","3","4").collect(Collectors.toList());

        for (String ind : list){
            System.out.println(ind);
            if ("2".equals(ind)){
                int ind3 = list.indexOf("3");
                int ind4 = list.indexOf("4");
                list.set(ind3,"4");
                list.set(ind4,"3");
            }
        }
        System.out.println(list);
    }

}