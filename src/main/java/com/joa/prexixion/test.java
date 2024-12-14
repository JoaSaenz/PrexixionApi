package com.joa.prexixion;

import org.apache.commons.lang3.StringUtils;

public class test {
    public static void main(String[] args) {
        System.out.println(StringUtils.leftPad("367890",9,'0'));
        System.out.println(StringUtils.leftPad("1234567890",9,'0'));
    }
}
