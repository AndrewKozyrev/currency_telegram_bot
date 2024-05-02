package com.skillbox.cryptobot.utils;

import java.math.BigDecimal;

public class TextUtil {

    public static String toString(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        } else {
            return String.format("%.3f", value);
        }
    }

    public static String toString(BigDecimal value) {
        return toString(value.doubleValue());
    }
}
