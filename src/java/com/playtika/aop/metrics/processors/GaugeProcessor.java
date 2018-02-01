package com.playtika.aop.metrics.processors;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.actuate.metrics.GaugeService;

/**
 * Default metrics processor based on Spring actuator {@link GaugeService}.
 *
 * @author vkhrushchak
 */
public class GaugeProcessor implements MetricsProcessor {

    private final String name;
    private final GaugeService gaugeService;
    private final AtomicReference<Long> reference;

    public GaugeProcessor(String name, GaugeService gaugeService) {
        this.name = name;
        this.gaugeService = gaugeService;
        this.reference = new AtomicReference<>();
    }

    public void start() {
        reference.set(System.currentTimeMillis());
    }

    public void complete() {
        gaugeService.submit(name, getPassedTime());
    }

    public void error(Throwable throwable) {
        long time = getPassedTime();
        gaugeService.submit(name + ".error", time);
        gaugeService.submit(name + "." + throwable.getClass().getSimpleName(), time);
    }

    private long getPassedTime() {
        return System.currentTimeMillis() - reference.get();
    }
}