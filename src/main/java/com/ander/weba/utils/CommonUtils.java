package com.ander.weba.utils;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouhh2 on 2016/12/14.
 */
public class CommonUtils {
    public static String getClassName(Class<?> cls) {
        return cls.getName().substring(cls.getName().lastIndexOf('.') + 1);
    }

    public static <T> T getObject(Class<T> object) {
        try {
            return object.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public static Object invokeMethod(Object src, String methodName,
                                      Class<?> param, Object value) throws Exception {
        Method method = null;
        try {
            method = param == null ? src.getClass().getDeclaredMethod(methodName)
                    : src.getClass().getDeclaredMethod(methodName, param);
        } catch (NoSuchMethodException e) {
            method = getAllDeclaredMethod(src, methodName, param);
        }
        method.setAccessible(true);
        return param == null ? method.invoke(src) : method.invoke(src, param.cast(value));
    }

    private static Method getAllDeclaredMethod(Object obj, String methodName, Class<?>... param) {
        Method method = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, param);
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return method;
    }

    public static String getNumber(String str) {
        String regEx = "[^0-9]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("").trim();
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Date getDate(String dateTimeStr, String formatStr) {
        try {
            if (dateTimeStr == null || dateTimeStr.equals("")) {
                throw new RuntimeException();
            }
            java.text.DateFormat sdf = new SimpleDateFormat(formatStr);
            Date date = sdf.parse(dateTimeStr);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
