package com.cmcciot.mat.elecalarm.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateTools
{
    /** <验证移动电话是否合法>
     * <功能详细描述>
     * @param mobile 移动电话
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isMobile(String mobile)
    {
        if (mobile == null || "".equals(mobile))
        {
            return false;
        }
        return Pattern.matches("^(0|(\\(\\d{3}\\))|(\\d{3}\\-))?13[456789]\\d{8}|14[7]\\d{8}|15[012789]\\d{8}|18[2378]\\d{8}",
                mobile);
    }
    
    /**
     * 验证用户名
     * 长度6-32个字符
     * 由字母（A-Z）、数字（0-9）、下划线（_）组成，字母不区分大小写，第一个字符是字母，最后一个字符不能是下划线
     * @param str
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isUserName(String str)
    {
        if (StringTools.isEmptyOrNull(str))
        {
            return false;
        }
        return Pattern.matches("^(?!.*?_$)[a-zA-Z]\\w{5,31}+$", str);
    }
    
    /**
     * 验证密码
     *  至少包含大写字母（A-Z）、小写字母（a-z），数字字符（0-9）中的两种，
     *  支持特殊字符：空格!’’ #$%&’ ()*+,-./:;<=>?@[\]^`{_|}~
     *  支持弱密码词典，密码不能是出现在弱密码词典中的词，弱密码词典系统可配
     * @param str
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isPassword(String str)
    {
        if (StringTools.isEmptyOrNull(str))
        {
            return false;
        }
        //包含大小写字母
        boolean booOne = Pattern.matches(".*?[a-z]+.*?", str)
                && Pattern.matches(".*?[A-Z]+.*?{5,31}", str);
        //包含小写与数字
        boolean booTwo = Pattern.matches(".*?[a-z]+.*?", str)
                && Pattern.matches(".*?[0-9]+.*?", str);
        //包含大写与数字
        boolean booThree = Pattern.matches(".*?[A-Z]+.*?", str)
                && Pattern.matches(".*?[0-9]+.*?", str);
        //包含大小写数字，包括特殊字符
        boolean booFour = Pattern.matches(".*?[A-Z]+.*?", str)
                && Pattern.matches(".*?[a-z]+.*?", str)
                && Pattern.matches(".*?[0-9]+.*?", str);
        
        return booOne || booTwo || booThree || booFour;
    }
    
    /**
     * <验证电话号码是否合法>
     * <功能详细描述>
     * @param str
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isMobileNum(String str)
    {
        if (str == null || "".equals(str))
        {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    
}
