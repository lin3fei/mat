package com.cmcciot.mat.elecalarm.common.util;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlTools
{
    
    /**
     * doc2String 将xml文档内容转为String
     * 
     * @return 字符串
     * @param document
     */
    public static String doc2String(Document document)
    {
        String str = "";
        try
        {
            // 使用输出流来进行转化
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 使用utf-8编码
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            str = out.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return str;
    }
    
    /**
     * string2Document 将字符串转为Document
     * 
     * @return doc
     * @param s
     */
    public static Document string2Document(String s)
    {
        Document doc = null;
        try
        {
            doc = DocumentHelper.parseText(s);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return doc;
    }
    
}
