package org.jeecgframework.boot.print.client.domain;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.boot.print.client.dto.OrderPrintTask;
import org.jeecgframework.boot.print.client.util.LoopRunner;


import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class Printer {

    public static LinkedBlockingQueue<OrderPrintTask> allOrderQueue = new LinkedBlockingQueue<>();

    PrintService printService;

    LinkedBlockingQueue<OrderPrintTask> manualOrderQueue = new LinkedBlockingQueue<>();

    LinkedBlockingQueue<OrderPrintTask> orderQueue = new LinkedBlockingQueue<>(1);

    LinkedBlockingQueue<String> imageQueue = new LinkedBlockingQueue<>();

    volatile boolean enable = false;

    volatile QueuedJobCount queuedJobCount;

    volatile OrderPrintTask currOrder;

    volatile String currImg;

    PrintListener printListener;

    LoopRunner putOrder = new LoopRunner(() -> {
        if (!enable) {
            ThreadUtil.sleep(1000);
            return;
        }
        LinkedBlockingQueue<OrderPrintTask> takeQueue = manualOrderQueue.isEmpty() ? allOrderQueue : manualOrderQueue;
        OrderPrintTask order = takeQueue.take();
        if (!enable) {
            takeQueue.put(order);
            return;
        }
        orderQueue.put(order);
        log.info("put order end: {}", JSONUtil.toJsonStr(order));
    });


    LoopRunner putImage = new LoopRunner(() -> {
        if (!imageQueue.isEmpty()) {
            ThreadUtil.sleep(1000);
            return;
        }
        OrderPrintTask order = orderQueue.take();
        currOrder = order;
        printListener.onStart(currOrder);
        List<String> images = order2Images(order);
        for (String image : images) {
            imageQueue.put(image);
            log.info("put image end: {}", image);
        }
    });


    PrintServiceAttributeListener onQueuedJobCountUpdate = new PrintServiceAttributeListener() {
        @Override
        public void attributeUpdate(PrintServiceAttributeEvent psae) {
//            PrinterIsAcceptingJobs printerIsAcceptingJobs = (PrinterIsAcceptingJobs) psae.getAttributes().get(PrinterIsAcceptingJobs.class);
//            System.out.println(printerIsAcceptingJobs);
            queuedJobCount = (QueuedJobCount) psae.getAttributes().get(QueuedJobCount.class);
            log.info("queued job count updated, count: {}", queuedJobCount.getValue());
            if (queuedJobCount.getValue() > 0) {
                return;
            }
            currImgEnd(null);
            requestPrintAsync();
        }
    };

    private void currImgEnd(Exception e) {
        //初始启动currImg为空
        if (currImg == null) {
            return;
        }

        if (e == null) {
            printListener.onImgFinish(currImg);
        } else {
            printListener.onImgError(currImg, e);
        }

        // todo 判断是否最后一张图
        if (currOrder.getImgUrls().getLast() == currImg) {
            printListener.onFinish(currOrder);
            currOrder = null;
        }
        currImg = null;
    }


    private List<String> order2Images(OrderPrintTask order) {
        return order.getImgUrls();
    }


    private void requestPrintAsync() {
        ThreadUtil.execute(() -> {
            String image;
            try {
                image = imageQueue.take();
            } catch (InterruptedException e) {
                log.error("ignored exception",e);
                return;
            }
            currImg = image;
            printListener.onImgStart(currImg);
            try {
                requestPrint(image);
            } catch (PrintException | FileNotFoundException e) {
                log.error("request print exception", e);
                currImgEnd(e);
                requestPrintAsync();
            }
        });
    }

    private void requestPrint(String image) throws FileNotFoundException, PrintException {
        DocPrintJob printJob = printService.createPrintJob();
        FileInputStream fileInputStream = new FileInputStream(image);
        SimpleDoc simpleDoc = new SimpleDoc(fileInputStream, DocFlavor.INPUT_STREAM.JPEG, null);
        HashPrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(OrientationRequested.PORTRAIT);
        attributeSet.add(new Copies(1));
//        MediaSize mediaSize = new MediaSize(89,127, Size2DSyntax.MM);
//        attributeSet.add(mediaSize);
        printJob.print(simpleDoc, null);
        log.info("request print end, image: {}", image);
    }


    public Printer(PrintService printService) {
        this.printService = printService;
        this.printService.addPrintServiceAttributeListener(onQueuedJobCountUpdate);
        ThreadUtil.execute(putOrder);
        ThreadUtil.execute(putImage);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    public PrintService getPrintService() {
        return printService;
    }

    public void setPrintListener(PrintListener printListener) {
        this.printListener = printListener;
    }

    public interface PrintListener {
        void onStart(OrderPrintTask order);

        void onFinish(OrderPrintTask order);

        void onImgStart(String img);

        void onImgFinish(String img);

        void onImgError(String img, Exception e);
    }
}
