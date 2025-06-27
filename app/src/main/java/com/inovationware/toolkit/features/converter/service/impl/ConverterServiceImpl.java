package com.inovationware.toolkit.features.converter.service.impl;

import com.inovationware.toolkit.features.converter.domain.DataUnit;
import com.inovationware.toolkit.features.converter.service.ConverterService;
import com.inovationware.toolkit.features.converter.strategy.ConverterStrategy;
import com.inovationware.toolkit.features.converter.strategy.impl.ConverterStrategyImpl;

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
