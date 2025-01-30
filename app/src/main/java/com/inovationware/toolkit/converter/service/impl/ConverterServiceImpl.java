package com.inovationware.toolkit.converter.service.impl;

import com.inovationware.toolkit.converter.domain.DataUnit;
import com.inovationware.toolkit.converter.service.ConverterService;
import com.inovationware.toolkit.converter.strategy.ConverterStrategy;
import com.inovationware.toolkit.converter.strategy.impl.ConverterStrategyImpl;

import org.apache.http.MethodNotSupportedException;

import java.math.BigDecimal;

public class ConverterServiceImpl implements ConverterService {
    private static ConverterServiceImpl instance;
    public static ConverterServiceImpl getInstance(){
        if(instance == null) instance = new ConverterServiceImpl();
        return instance;
    }
    private ConverterServiceImpl(){
        strategy = ConverterStrategyImpl.getInstance();
    }

    private ConverterStrategy strategy;
    @Override
    public BigDecimal convert(double from, DataUnit fromUnit, DataUnit toUnit) throws UnsupportedOperationException {
        return strategy.convert(from, fromUnit, toUnit);
    }
}
