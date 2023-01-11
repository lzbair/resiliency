package io.patterns.circuitbreaker;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Boot {

    static final Logger LOG = LoggerFactory.getLogger(Boot.class);

    public static void main(String[] args) throws Exception {

        HystrixCommand.Setter v1Config = config("webapi/v1");
        HystrixCommand.Setter v2Config = config("webapi/v2");


        ExecutorService exec = Executors.newFixedThreadPool(10);

        List<Callable<String>> tasks = new ArrayList<>();


        for (int i = 0; i < 1000; i++) {
            tasks.add(() -> new ApiCommand(v1Config, new RemoteApi()).execute());
        }


        for (Future<String> future : exec.invokeAll(tasks)) {
            try {
                LOG.info(future.get());
            } catch (Exception e) {
                LOG.error("ERROR ---->: ", e);
            }
        }


    }

    private static HystrixCommand.Setter config(String apiVersion) {
        HystrixCommand.Setter config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(apiVersion));
        config.andCommandKey(HystrixCommandKey.Factory.asKey(apiVersion));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(1000);
        commandProperties.withCircuitBreakerEnabled(true);
        commandProperties.withCircuitBreakerSleepWindowInMilliseconds(5000);
        commandProperties.withCircuitBreakerRequestVolumeThreshold(1);
        config.andCommandPropertiesDefaults(commandProperties);

        //Thread pooling
        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withMaxQueueSize(2)
                .withCoreSize(1)
                .withQueueSizeRejectionThreshold(2));
        return config;
    }
}
