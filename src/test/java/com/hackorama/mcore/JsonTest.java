package com.hackorama.mcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hackorama.mcore.common.Util;

public class JsonTest {

    @Test
    public void jsonGeneralaFormattingTests() {
        List<String> listOne = new ArrayList<>();
        listOne.add("one");
        listOne.add("two");
        listOne.add("three");
        List<String> listTwo = new ArrayList<>();
        listTwo.add("1");
        listTwo.add("2");
        listTwo.add("3");
        Map<String, List<String>> map = new HashMap<>();
        map.put("ONE", listOne);
        map.put("TWO", listTwo);
        System.out.println(Util.toJsonString("array one", listOne));
        System.out.println(Util.toJsonString("array two", listTwo));
        System.out.println(Util.toJsonString("map", map));
    }

}
