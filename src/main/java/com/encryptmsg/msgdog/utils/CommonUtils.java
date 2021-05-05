package com.encryptmsg.msgdog.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {

    public static String joinString(String originString, String appendString) {
        String[] splitted = new String[]{};
        List<String> splittedList = new ArrayList<>();
        if (StringUtils.isNotBlank(originString)) {
            if (originString.contains(",")) {
                splitted = StringUtils.split(originString, ",");
            } else {
                splittedList.add(originString);
            }
        }
        if (ArrayUtils.isNotEmpty(splitted)) {
            splittedList.addAll(Arrays.asList(splitted));
        }
        if (StringUtils.isNotBlank(appendString)) {
            splittedList.add(appendString);
        }
        String[] out = splittedList.stream().toArray(String[]::new);
        return StringUtils.join(out, ",");
    }

    public static List<String> parseStringList(String originString) {
        List<String> out = new ArrayList<>();
        if (StringUtils.isBlank(originString)) {
            return out;
        }
        if (!originString.contains(",")) {
            out.add(originString);
        } else {
            String[] splitted = StringUtils.split(originString, ",");
            out.addAll(Arrays.asList(splitted));
        }
        return out;

    }
}
