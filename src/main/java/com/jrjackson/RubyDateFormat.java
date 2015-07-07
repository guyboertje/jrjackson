/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jrjackson;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author guy
 */
public class RubyDateFormat extends SimpleDateFormat{

    public RubyDateFormat() {
    }

    public RubyDateFormat(String pattern) {
        super(pattern);
    }

    public RubyDateFormat(String pattern, Locale locale) {
        super(pattern, locale);
    }

    public RubyDateFormat(String pattern, DateFormatSymbols formatSymbols) {

        super(pattern, formatSymbols);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        try {
            throw new IllegalArgumentException("test test test");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return toAppendTo;
    }
}
