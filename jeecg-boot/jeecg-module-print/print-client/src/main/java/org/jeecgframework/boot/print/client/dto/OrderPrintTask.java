package org.jeecgframework.boot.print.client.dto;

import lombok.Data;

import java.util.List;


@Data
public class OrderPrintTask {


    private String orderSn;

    private List<String> imgUrls;

    private Integer perPagePrintNum;
}
