package com.ksyun.campus.metaserver.util;

import java.util.ArrayList;
import java.util.List;

public class MyStringUtil {
    public static List<String> splitstr(String str , String splitchar)//将字符串按照splitchar进行分割
    {
        String[] parts = str.split(splitchar);
        List<String> fruitList = new ArrayList<>();
        for (String part : parts) {
            fruitList.add(part);
        }
        return fruitList;
    }
    public static String splitlaststr(String str, String splitchar)//将字符串按照splitchar进行分割，返回最后一个
    {
        String[] parts = str.split(splitchar);
        List<String> fruitList = new ArrayList<>();
        for (String part : parts) {
            fruitList.add(part);
        }
        return fruitList.get(fruitList.size()-1);
    }

}
