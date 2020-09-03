package com.example.cprmtest.demo.integrationtest;
import com.example.cprmtest.demo.marketdata.sources.mockimpl.RNGWrapper;
import com.example.cprmtest.demo.portfolio.services.notifier.PushServiceWrapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

//config created to acommodate for @PostConstruct
@TestConfiguration
public class NotificationTestConfig {

    @Bean
    public PushServiceWrapper<Double> pushServiceWrapper(){
        PushServiceWrapper<Double> wrapper = Mockito.mock(PushServiceWrapper.class);
        return wrapper;
    }

    //if time delay requested then return 1.5, else return 220
    @Bean
    public RNGWrapper rngWrapper(){
        return new RNGWrapper() {
            @Override
            public double getInverseNormal() {
                return 0.4;
            }

            @Override
            public double getBetweenRange(double low, double high) {
                if (low >= 0.0 && high <= 2.5) {
                    return 1.5;
                } else {
                    return 220.0;
                }
            }
        };
    }
}
