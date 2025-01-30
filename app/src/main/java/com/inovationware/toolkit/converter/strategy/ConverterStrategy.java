package com.inovationware.toolkit.converter.strategy;

import com.inovationware.toolkit.converter.domain.DataUnit;

import org.apache.http.MethodNotSupportedException;

import java.math.BigDecimal;

public interface ConverterStrategy {
    BigDecimal convert(double from, DataUnit fromUnit, DataUnit toUnit) throws UnsupportedOperationException;
}
