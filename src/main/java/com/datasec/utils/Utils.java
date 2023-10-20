package com.datasec.utils;

import com.datasec.utils.enums.ColourTypePrintValueEnum;
import com.datasec.utils.enums.PrinterParamsEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import static com.datasec.utils.enums.ColourTypePrintValueEnum.*;
import static com.datasec.utils.enums.PageSizeValueEnum.*;
import static com.datasec.utils.enums.PrintQualityValueEnum.*;
import static com.datasec.utils.enums.PrinterParamsEnum.*;

public class Utils {

    public static PrinterParamsEnum fromStringtoPrinterParamEnum (String s) {
        PrinterParamsEnum en;
        switch (s){
            case "COLOUR_TYPE_PRINT":
                en = COLOUR_TYPE_PRINT;
                break;
            case "PRINT_QUALITY":
                en = PRINT_QUALITY;
                break;
            case "PAGE_SIZE":
                en = PAGE_SIZE;
                break;
            case "IS_PAGE_ORIENTATION_VERTICAL":
                en = IS_PAGE_ORIENTATION_VERTICAL;
                break;
            case "IS_DOUBLE_SIDED":
                en = IS_DOUBLE_SIDED;
                break;
            case "INK_LEVEL":
                en = INK_LEVEL;
                break;
            default:
                en = null;
                break;
        }
        return en;
    }

    public static boolean configParamExists(String input) {
        try {
            // Attempt to parse the string as an enum
            PrinterParamsEnum.valueOf(input);
            // If the parsing is successful, the string matches an enum value
            return true;
        } catch (IllegalArgumentException e) {
            // If an IllegalArgumentException is thrown, the string does not match any enum value
        }
        return false;
    }

    public static boolean checkAndPutValueInConfig (HashMap<PrinterParamsEnum, Object> configPrinter, String parameter, String value) {
        if (!StringUtils.isEmpty(parameter) || !StringUtils.isBlank(parameter) || !StringUtils.isEmpty(value) || !StringUtils.isBlank(value)){
            switch (parameter){
                case "COLOUR_TYPE_PRINT":
                    return checkAndPutColourTypeValue(configPrinter, value);
                case "PRINT_QUALITY":
                    return checkAndPutPrintQualityValue(configPrinter, value);
                case "PAGE_SIZE":
                    return checkAndPutPageSizeValue(configPrinter, value);
                case "IS_PAGE_ORIENTATION_VERTICAL":
                case "IS_DOUBLE_SIDED":
                    return checkAndPutBooleanTypeValue(configPrinter, value);
                case "INK_LEVEL":
                    return checkAndPutIntegerTypeValue(configPrinter, value);
            }
        }
        return false;
    }

    public static boolean checkAndPutColourTypeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("BLACK_AND_WHITE")){
            configPrinter.put(COLOUR_TYPE_PRINT, BLACK_AND_WHITE);
            return true;
        }
        if (value.equalsIgnoreCase("COLOUR")){
            configPrinter.put(COLOUR_TYPE_PRINT, COLOUR);
            return true;
        }
        return false;
    }

    public static boolean checkAndPutPrintQualityValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("MAXIMUM")){
            configPrinter.put(PRINT_QUALITY, MAXIMUM);
            return true;
        }
        if (value.equalsIgnoreCase("HIGH")){
            configPrinter.put(PRINT_QUALITY, HIGH);
            return true;
        }
        if (value.equalsIgnoreCase("MEDIUM")){
            configPrinter.put(PRINT_QUALITY, MEDIUM);
            return true;
        }
        if (value.equalsIgnoreCase("LOW")){
            configPrinter.put(PRINT_QUALITY, LOW);
            return true;
        }
        return false;
    }

    public static boolean checkAndPutPageSizeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("A2")){
            configPrinter.put(PAGE_SIZE, A2);
            return true;
        }
        if (value.equalsIgnoreCase("A3")){
            configPrinter.put(PAGE_SIZE, A3);
            return true;
        }
        if (value.equalsIgnoreCase("A4")){
            configPrinter.put(PAGE_SIZE, A4);
            return true;
        }
        if (value.equalsIgnoreCase("A5")){
            configPrinter.put(PAGE_SIZE, A5);
            return true;
        }
        if (value.equalsIgnoreCase("LETTER")){
            configPrinter.put(PAGE_SIZE, LETTER);
            return true;
        }
        return false;
    }

    public static boolean checkAndPutBooleanTypeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("true")){
            configPrinter.put(IS_DOUBLE_SIDED, true);
            return true;
        }
        if (value.equalsIgnoreCase("false")){
            configPrinter.put(IS_DOUBLE_SIDED, false);
            return true;
        }
        return false;
    }


    public static boolean checkAndPutIntegerTypeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        try {
            // Convert the string to an integer
            int intValue = Integer.parseInt(value);

            if (intValue <= 100 && intValue > (Integer)configPrinter.get(INK_LEVEL)) {
                configPrinter.put(INK_LEVEL, intValue);
                return true;
            }
        } catch (NumberFormatException e) {
            // Handle the exception if the string cannot be parsed as an integer
            System.err.println("Unable to convert the string to an integer.");
        }
        return false;
    }


}
