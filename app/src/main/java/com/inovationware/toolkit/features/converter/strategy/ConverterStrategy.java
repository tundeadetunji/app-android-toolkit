package com.inovationware.toolkit.features.converter.strategy;

import com.inovationware.toolkit.features.converter.domain.DataUnit;

import java.math.BigDecimal;

public interface ConverterStrategy {
    BigDecimal convert(double from, DataUnit fromUnit, DataUnit toUnit) throws UnsupportedOperationException;
}
