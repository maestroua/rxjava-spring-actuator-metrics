package com.playtika.aop.metrics;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import rx.Completable;
import rx.Observable;
import rx.Single;

import com.playtika.aop.metrics.annotations.Gauge;
import com.playtika.aop.metrics.processors.MetricsProcessor;

import static rx.Completable.fromAction;

/**
 * Interceptor for {@link Gauge} annotation.
 * Support synchronous methods and async methods
 * that return rx java types {@link Observable}, {@link Single}, {@link Completable}.
 *
 * @author vkhrushchak
 */
public class MetricsInterceptor implements MethodInterceptor {

    private final Function<String, MetricsProcessor> processorSupplier;

    public MetricsInterceptor(Function<String, MetricsProcessor> processorSupplier) {
        this.processorSupplier = processorSupplier;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        Gauge annotation = method.getAnnotation(Gauge.class);
        String name = getName(method, annotation.value());

        MetricsProcessor processor = processorSupplier.apply(name);
        if (isAssignableFrom(method, Observable.class)) {
            return ((Observable<Object>) invocation.proceed())
                    .compose(processObservable(processor));
        } else if (isAssignableFrom(method, Single.class)) {
            return ((Single<Object>) invocation.proceed())
                    .compose(processSingle(processor));
        } else if (isAssignableFrom(method, Completable.class)) {
            return ((Completable) invocation.proceed())
                    .compose(processCompletable(processor));
        } else {
            processor.start();
            try {
                Object obj = invocation.proceed();
                processor.complete();
                return obj;
            } catch (Exception ex) {
                processor.error(ex);
                throw ex;
            }
        }
    }

    private <T> Observable.Transformer<T, T> processObservable(MetricsProcessor processor) {
        return observable -> fromAction(processor::start)
                .andThen(observable)
                .doOnCompleted(processor::complete)
                .doOnError(processor::error);
    }

    private <T> Single.Transformer<T, T> processSingle(MetricsProcessor processor) {
        return single -> fromAction(processor::start)
                .andThen(single)
                .doOnSuccess(ignore -> processor.complete())
                .doOnError(processor::error);
    }

    private Completable.Transformer processCompletable(MetricsProcessor processor) {
        return completable -> fromAction(processor::start)
                .andThen(completable)
                .doOnCompleted(processor::complete)
                .doOnError(processor::error);
    }

    private String getName(Method method, String name) {
        return name.isEmpty() ? method.getDeclaringClass().getCanonicalName() + "." + method.getName() : name;
    }

    private boolean isAssignableFrom(Method method, Class<?> clazz) {
        return clazz.isAssignableFrom(method.getReturnType());
    }
}