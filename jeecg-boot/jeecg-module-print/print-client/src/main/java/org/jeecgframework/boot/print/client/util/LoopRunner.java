package org.jeecgframework.boot.print.client.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoopRunner implements Runnable{

    private final RunnableWithException loop;

    public LoopRunner(RunnableWithException loop) {
        this.loop = loop;
    }

    @Override
    public void run() {
        while (true) {
            try {
                loop.run();
            } catch (Exception e) {
                log.error("ignored exception", e);
            }
        }
    }

    public interface RunnableWithException {
        void run() throws Exception;
    }
}
