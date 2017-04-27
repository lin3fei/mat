package com.cmcciot.mat.common.utils;


public class PostParameter implements java.io.Serializable
{
    String name;
    
    String value;
    
    private static final long serialVersionUID = -8708108746980739212L;
    
    public PostParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public PostParameter(String name, long value)
    {
        this.name = name;
        this.value = String.valueOf(value);
    }
    
    public PostParameter(String name, double value)
    {
        this.name = name;
        this.value = String.valueOf(value);
    }
    
    public PostParameter(String name, int value)
    {
        this.name = name;
        this.value = String.valueOf(value);
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
}
