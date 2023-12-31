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
                    return checkAndPutBooleanOrientationTypeValue(configPrinter, value);
                case "IS_DOUBLE_SIDED":
                    return checkAndPutBooleanDoubleSidedTypeValue(configPrinter, value);
                case "INK_LEVEL":
                    return checkAndPutInkLeveleValue(configPrinter, value);
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

    public static boolean checkAndPutBooleanDoubleSidedTypeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
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

    public static boolean checkAndPutBooleanOrientationTypeValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("true")){
            configPrinter.put(IS_PAGE_ORIENTATION_VERTICAL, true);
            return true;
        }
        if (value.equalsIgnoreCase("false")){
            configPrinter.put(IS_PAGE_ORIENTATION_VERTICAL, false);
            return true;
        }
        return false;
    }


    public static boolean checkAndPutInkLeveleValue(HashMap<PrinterParamsEnum, Object> configPrinter, String value) {
        if (value.equalsIgnoreCase("FULL")){
            configPrinter.put(INK_LEVEL, 100);
            return true;
        }
        if (value.equalsIgnoreCase("HALF")){
            configPrinter.put(INK_LEVEL, 50);
            return true;
        }
        if (value.equalsIgnoreCase("LOW")){
            configPrinter.put(INK_LEVEL, 20);
            return true;
        }
        if (value.equalsIgnoreCase("EMPTY")){
            configPrinter.put(INK_LEVEL, 0);
            return true;
        }
        return false;
    }


}
