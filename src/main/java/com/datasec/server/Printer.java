package com.datasec.server;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import com.datasec.utils.enums.PrinterParamsEnum;
import com.datasec.utils.enums.PrinterStatusEnum;

import static com.datasec.utils.enums.ColourTypePrintValueEnum.BLACK_AND_WHITE;
import static com.datasec.utils.enums.PageSizeValueEnum.A4;
import static com.datasec.utils.enums.PrintQualityValueEnum.HIGH;
import static com.datasec.utils.enums.PrinterParamsEnum.*;
import static com.datasec.utils.enums.PrinterStatusEnum.*;

@Getter
@Setter
public class Printer {
    String namePrinter;
    Boolean isRunning;
    PrinterStatusEnum statusPrinter;
    ArrayList<JobInQueue> queuePrinter;
    HashMap<PrinterParamsEnum, Object> configPrinter;

    public Printer(String name) {
        setNamePrinter(StringUtils.isEmpty(name) || StringUtils.isBlank(name) ? "generic_printer" : name);
        setStatusPrinter(READY_TO_PRINT);
        setIsRunning(true);
        setQueuePrinter(new ArrayList<JobInQueue>());
        setConfigPrinter(setDefaultPrinterConfig());
    }

    private HashMap<PrinterParamsEnum, Object> setDefaultPrinterConfig() {
        HashMap<PrinterParamsEnum, Object> configs = new HashMap<PrinterParamsEnum, Object>();
        configs.put(COLOUR_TYPE_PRINT, BLACK_AND_WHITE);
        configs.put(PRINT_QUALITY, HIGH);
        configs.put(PAGE_SIZE, A4);
        configs.put(IS_PAGE_ORIENTATION_VERTICAL, true);
        configs.put(IS_DOUBLE_SIDED, false);
        configs.put(INK_LEVEL, 100);

        return configs;
    }

    private boolean updateInkLevelSuccessful() {
        if ((Integer) this.getConfigPrinter().get(INK_LEVEL) > 0) {
            this.getConfigPrinter().put(INK_LEVEL, (Integer) this.getConfigPrinter().get(INK_LEVEL) - 1);
            return true;
        }

        this.setStatusPrinter(ERROR_INK_NEEDED);
        System.out.println("there is no ink, reload needed");
        return false;
    }

    public void print(String filename) {
        if (updateInkLevelSuccessful()) {
            System.out.println(this.getNamePrinter() + "-> printing... :" + filename);
            this.setStatusPrinter(PRINTING);
//        try {
//            // Sleep for 5 seconds (5000 milliseconds)
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            // Handle the exception if it occurs
//            e.printStackTrace();
//        }
            System.out.println(this.getNamePrinter() + "-> finished print... :" + filename);
            this.setStatusPrinter(JOB_FINISHED);
        }
    }

}
