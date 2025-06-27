package com.inovationware.toolkit.common.model;

public enum TextCase
{
    Capitalize(0),
    UpperCase(1),
    LowerCase(2),
    None(3);

    private int intValue;
    private static java.util.HashMap<Integer, TextCase> mappings;
    private static java.util.HashMap<Integer, TextCase> getMappings()
    {
        if (mappings == null)
        {
            synchronized (TextCase.class)
            {
                if (mappings == null)
                {
                    mappings = new java.util.HashMap<Integer, TextCase>();
                }
            }
        }
        return mappings;
    }

    private TextCase(int value)
    {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue()
    {
        return intValue;
    }

    public static TextCase forValue(int value)
    {
        return getMappings().get(value);
    }
}
