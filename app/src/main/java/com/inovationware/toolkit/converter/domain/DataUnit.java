package com.inovationware.toolkit.converter.domain;

import android.os.Build;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum DataUnit {
    Bit,
    Byte,
    Kilobyte,
    Megabyte;

    public static String[] toStringArray(){
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (String[]) Arrays.stream(DataUnit.values()).map(Enum::toString).toArray();
        }*/
        List<String> result = new ArrayList<>();
        for(DataUnit unit : DataUnit.values()){
            result.add(unit.toString());
        }
        return result.toArray(new String[DataUnit.values().length]);
    }

    public static String toPlural(BigDecimal count, DataUnit unit){
        return Objects.equals(count, BigDecimal.ONE) ? unit.name() : unit.name() + "s";
    }

    public static String toPlural(BigDecimal count, String unit){
        return Objects.equals(count, BigDecimal.ONE) ? unit : unit + "s";
    }

}
