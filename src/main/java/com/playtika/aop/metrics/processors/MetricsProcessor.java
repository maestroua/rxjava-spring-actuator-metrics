package com.playtika.aop.metrics.processors;

public interface MetricsProcessor {

    void start();

    void complete();

    void error(Throwable throwable);
}
