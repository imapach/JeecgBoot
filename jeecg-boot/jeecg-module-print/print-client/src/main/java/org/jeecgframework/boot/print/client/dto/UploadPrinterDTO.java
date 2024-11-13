package org.jeecgframework.boot.print.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class UploadPrinterDTO {
    private String host;
    private List<Printer> printers;

    @Data
    public static class Printer {
        private Integer id;
        private String name;
    }

}
