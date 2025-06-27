package com.inovationware.toolkit.features.converter.strategy.impl;

import com.inovationware.toolkit.features.converter.domain.DataUnit;
import com.inovationware.toolkit.features.converter.strategy.ConverterStrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ConverterStrategyImpl implements ConverterStrategy {
    private static ConverterStrategyImpl instance;
    public static ConverterStrategyImpl getInstance(){
        if(instance == null) instance = new ConverterStrategyImpl();
        return instance;
    }
    private ConverterStrategyImpl(){
        initializeValues();
    }

    private void initializeValues() {
        conversions = new HashMap<>();

        conversions.put(DataUnit.Bit.name() + DataUnit.Byte.name(), BigDecimal.valueOf(0.125));
        conversions.put(DataUnit.Byte.name() + DataUnit.Bit.name(), BigDecimal.valueOf(8));

        conversions.put(DataUnit.Bit.name() + DataUnit.Kilobyte.name(), BigDecimal.valueOf(0.000125));
        conversions.put(DataUnit.Kilobyte.name() + DataUnit.Bit.name(), BigDecimal.valueOf(8000));

        conversions.put(DataUnit.Bit.name() + DataUnit.Megabyte.name(), BigDecimal.valueOf(0.000000125));
        conversions.put(DataUnit.Megabyte.name() + DataUnit.Bit.name(), BigDecimal.valueOf(8000000));
    }

    private Map<String, BigDecimal> conversions;

    @Override
    public BigDecimal convert(double from, DataUnit fromUnit, DataUnit toUnit) throws UnsupportedOperationException {
        if (fromUnit == toUnit) return BigDecimal.valueOf(from);

        try{
            return BigDecimal.valueOf(from).multiply(conversions.get(fromUnit.name() + toUnit.name()));
        }
        catch (Exception exception){
            throw new UnsupportedOperationException("Cannot convert from " + toUnit.name() + " to " + fromUnit.name() + " yet. Try converting from Bit only.");
        }
    }
}
