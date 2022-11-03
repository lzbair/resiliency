package io.patterns.circuitbreaker;

import com.netflix.hystrix.HystrixCommand;

public class ApiCommand extends HystrixCommand<String> {

    private final RemoteApi delegate;

    public ApiCommand(Setter setter, RemoteApi delegate) {
        super(setter);
        this.delegate = delegate;
    }

    @Override
    protected String run() throws Exception {
        return delegate.probe();
    }


    @Override
    protected String getFallback() {
        return delegate.fallback();
    }
}
