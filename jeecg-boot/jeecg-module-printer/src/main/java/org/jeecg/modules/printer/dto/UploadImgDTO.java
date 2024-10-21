package org.jeecg.modules.printer.dto;

import lombok.Data;

import java.util.List;


@Data
public class UploadImgDTO {

    private String shopId;

    private String orderId;

    private List<String> imgOssIds;


}
