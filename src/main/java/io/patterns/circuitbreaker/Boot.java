package io.patterns.circuitbreaker;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Boot {

    static final Logger LOG = LoggerFactory.getLogger(Boot.class);

    public static void main(String[] args) throws Exception {
        HystrixCommand.Setter config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("webapi"));
        config.andCommandKey(HystrixCommandKey.Factory.asKey("webapi/v1"));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(1000);
        commandProperties.withCircuitBreakerEnabled(true);
        commandProperties.withCircuitBreakerSleepWindowInMilliseconds(5000);
        commandProperties.withCircuitBreakerRequestVolumeThreshold(1);
        config.andCommandPropertiesDefaults(commandProperties);


        while(true) {
            ApiCommand apiCommand = new ApiCommand(config, new RemoteApi());
            try {
                LOG.info(apiCommand.execute());
            }catch (Exception e){
                LOG.error(e.getMessage());
            }
        }
    }
}
