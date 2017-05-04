/*
 * 文 件 名:  AESTools.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  lidechun
 * 修改时间:  2014年4月15日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.cmcciot.mat.elecalarm.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  lidechun
 * @version  [版本号, 2014年4月15日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class AESTools
{
    /**
     * <AES加密>
     * <功能详细描述>
     * @param content 需要加密的内容
     * @return String 加密结果
     * 
     */
    public static String encrypt(String content)
    {
        KeyGenerator kgen;
        try
        {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"); 
            
            secureRandom.setSeed(new String().getBytes());
            
            kgen = KeyGenerator.getInstance("AES");
            
            kgen.init(128, secureRandom);
            
            SecretKey secretKey = kgen.generateKey();
            
            byte[] enCodeFormat = secretKey.getEncoded();
            
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            byte[] byteContent = content.getBytes("utf-8");
            
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] result = cipher.doFinal(byteContent);
            
            // 加密
            return parseByte2HexStr(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * <AES加密>
     * <功能详细描述>
     * @param content 需要加密的内容
     * @param password 加密密钥
     * @return String 加密结果
     * 
     */
    public static String encrypt(String content, String password)
    {
        try
        {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"); 
            
            secureRandom.setSeed(password.getBytes());
            
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            
            kgen.init(128, secureRandom);
            
            SecretKey secretKey = kgen.generateKey();
            
            byte[] enCodeFormat = secretKey.getEncoded();
            
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            byte[] byteContent = content.getBytes("utf-8");
            
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] result = cipher.doFinal(byteContent);
            
            // 加密
            return parseByte2HexStr(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * <AES解密>
     * <功能详细描述>
     * @param content 待解密内容
     * @return String 解密结果
     * 
     */
    public static String decrypt(String content)
    {
        try
        {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"); 
            
            secureRandom.setSeed(new String().getBytes());
            
            byte[] decryptFrom = parseHexStr2Byte(content);
            
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            
            kgen.init(128, secureRandom);
            
            SecretKey secretKey = kgen.generateKey();
            
            byte[] enCodeFormat = secretKey.getEncoded();
            
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            byte[] result = cipher.doFinal(decryptFrom);
            
            // 解密
            return new String(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * <AES解密>
     * <功能详细描述>
     * @param content 待解密内容
     * @param password 解密密钥
     * @return String 解密结果
     * 
     */
    public static String decrypt(String content, String password)
    {
        try
        {
            byte[] decryptFrom = parseHexStr2Byte(content);
            
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG"); 
            
            secureRandom.setSeed(password.getBytes());
            
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            
            kgen.init(128, secureRandom);
            
            SecretKey secretKey = kgen.generateKey();
            
            byte[] enCodeFormat = secretKey.getEncoded();
            
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            byte[] result = cipher.doFinal(decryptFrom);
            
            // 解密
            return new String(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[])
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            try
            {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1)
                {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr)
    {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++)
        {
            try
            {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1),
                        16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1,
                        i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        return result;
    }
    
}
