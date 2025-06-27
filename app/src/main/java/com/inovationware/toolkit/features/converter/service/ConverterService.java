package com.inovationware.toolkit.features.converter.service;

import com.inovationware.toolkit.features.converter.domain.DataUnit;

import java.math.BigDecimal;

public interface ConverterService {
    BigDecimal convert(double from, DataUnit fromUnit, DataUnit toUnit) throws UnsupportedOperationException;
}
