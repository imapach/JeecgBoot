package org.jeecgframework.boot.print.client.service;


import org.jeecgframework.boot.print.client.domain.Printer;
import org.jeecgframework.boot.print.client.dto.OrderPrintTask;
import org.springframework.stereotype.Component;


@Component
public class MyPrintListener implements Printer.PrintListener {



    @Override
    public void onStart(OrderPrintTask order) {

    }

    @Override
    public void onFinish(OrderPrintTask order) {

    }

    @Override
    public void onImgStart(String img) {

    }

    @Override
    public void onImgFinish(String img) {

    }

    @Override
    public void onImgError(String img, Exception e) {

    }
}
