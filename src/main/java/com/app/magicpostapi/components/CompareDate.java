package com.app.magicpostapi.components;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CompareDate {
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Chuyển đổi ngày thành chuỗi để so sánh
        String dateString1 = dateFormat.format(date1);
        String dateString2 = dateFormat.format(date2);

        // Sử dụng equals để kiểm tra xem hai chuỗi ngày có giống nhau hay không
        return dateString1.equals(dateString2);
    }
}
