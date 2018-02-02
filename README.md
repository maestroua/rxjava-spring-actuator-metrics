# Spring Actuator annotation-based metrics collector with RxJava support
Simple library for spring boot actuator to collect method execution statistic.

Usage example:
```
@Gauge("timer.data.getAll")
public Observable<Data> getAll() {
  return readFromDb()
    .doOnNext(data -> log.debug("Data {} acquired", data))
}
```

With Dropwizard metrics will be gathered:
```
{
  "timer.data.getAll.fiveMinuteRate": 2.1999999999999997,
  "timer.data.getAll.snapshot.min": 56,
  "timer.data.getAll.snapshot.99thPercentile": 63,
  "timer.data.getAll.snapshot.max": 63,
  "timer.data.getAll.oneMinuteRate": 2.1999999999999997,
  "timer.data.getAll.snapshot.999thPercentile": 63,
  "timer.data.getAll.fifteenMinuteRate": 2.1999999999999997,
  "timer.data.getAll.count": 11,
  "timer.data.getAll.snapshot.stdDev": 1,
  "timer.data.getAll.snapshot.98thPercentile": 63,
  "timer.data.getAll.snapshot.75thPercentile": 59,
  "timer.data.getAll.snapshot.95thPercentile": 63,
  "timer.data.getAll.meanRate": 1.9903309638543931,
  "timer.data.getAll.snapshot.mean": 58,
  "timer.data.getAll.snapshot.median": 58
}
```

<img src="https://travis-ci.org/maestroua/rxjava-spring-actuator-metrics.svg?branch=master">
