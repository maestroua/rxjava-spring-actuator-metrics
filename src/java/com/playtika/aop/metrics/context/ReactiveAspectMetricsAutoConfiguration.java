package com.playtika.aop.metrics.context;

import java.util.function.Function;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.playtika.aop.metrics.MetricsInterceptor;
import com.playtika.aop.metrics.processors.GaugeProcessor;
import com.playtika.aop.metrics.processors.MetricsProcessor;

@Configuration
public class ReactiveAspectMetricsAutoConfiguration {

    @Bean
    @ConditionalOnClass(GaugeService.class)
    Function<String, MetricsProcessor> metricsProcessorSupplier(GaugeService gaugeService) {
        return name -> new GaugeProcessor(name, gaugeService);
    }

    @Bean
    MetricsInterceptor metricsInterceptor(Function<String, MetricsProcessor> metricsProcessorSupplier) {
        return new MetricsInterceptor(metricsProcessorSupplier);
    }

    @Bean
    Advisor meteredAdvisor(MetricsInterceptor metricsInterceptor) {
        AspectJExpressionPointcut advisor = new AspectJExpressionPointcut();
        advisor.setExpression("@annotation(com.playtika.aop.metrics.annotations.Gauge)");
        return new DefaultPointcutAdvisor(advisor, metricsInterceptor);
    }
}