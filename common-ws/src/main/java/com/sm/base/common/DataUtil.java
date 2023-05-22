package com.sm.base.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;


public class DataUtil {
    public static String objectToJson(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(o);
    }
    public static boolean isNullOrEmpty(CharSequence cs) {
        return nullOrEmpty(cs);
    }

    public static boolean nullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullObject(Object object) {
        if(object == null){
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(List<Object> obj1) {
        return obj1 == null || obj1.isEmpty();
    }

    public static boolean isNullOrEmpty(Long obj1) {
        return obj1 == null || obj1 == 0;
    }

    public static Long getSequence(EntityManager entityManager, String sequenceName) {
        String sql = "select " + sequenceName + ".nextval from dual";
        Query query = entityManager.createNativeQuery(sql);
        return ((BigDecimal) query.getSingleResult()).longValue();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s == "";
    }


    public static String getString (Object obj){
        if(obj == null){
            return "";
        }
        return obj.toString().trim();
    }

    public static String getString (String s){
        if(isNullOrEmpty(s)){
            return "";
        }
        return s.trim();
    }

    public static Long getLong (Long l){
        if(l == 0L || l == null){
            return 0L;
        }
        return l;
    }

    public static LocalDateTime stringToLocalDateTme(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
    }

    public static String getSystemDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    public static String getStringDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = date.format(formatter).toString();
        return formattedDateTime;
    }


}
