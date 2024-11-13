package org.jeecgframework.boot.print.client.service;

import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.boot.print.client.domain.Printer;
import org.jeecgframework.boot.print.client.dto.EnableDTO;
import org.jeecgframework.boot.print.client.dto.OrderPrintTask;
import org.jeecgframework.boot.print.client.dto.PrinterVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.*;
import java.util.LinkedHashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/printer")
public class PrinterController implements InitializingBean {

    LinkedHashMap<String, Printer> printers = new LinkedHashMap<>();

    @Autowired
    MyPrintListener myPrintListener;

    @Override
    public void afterPropertiesSet() throws Exception {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            Printer printer = new Printer(printService);
            printer.setPrintListener(myPrintListener);
            printers.put(printService.getName(), printer);
        }
    }

    @GetMapping("/printers")
    public List<PrinterVO> printers() {
        return printers.values().stream().map(printer->{
            PrinterVO printerVO = new PrinterVO();
            printerVO.setName(printer.getPrintService().getName());
            printerVO.setEnable(printer.isEnable());
            return printerVO;
        }).toList();
    }


    @PostMapping("/enable")
    public void enable(@RequestBody EnableDTO enable) {
        printers.get(enable.getName()).setEnable(enable.getEnable());
    }

    @PostMapping("/addOrder")
    public void addOrder(@RequestBody OrderPrintTask order) throws InterruptedException {
        Printer.allOrderQueue.put(order);
    }







}
