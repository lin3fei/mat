package com.cmcciot.mat.enclosure.tools;

import com.caucho.hessian.client.HessianProxyFactory;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Administrator on 2015/8/11.
 */
public class HessianProxyUtil {
    private static Logger logger = LoggerFactory.getLogger(HessianProxyUtil.class);

    @SuppressWarnings("rawtypes")
    public static Object getHessianProxyObject(String url, Class cls) {

        Object obj = null;

        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            factory.setOverloadEnabled(true);
            obj = factory.create(cls, url);
        } catch (MalformedURLException e) {
            logger.error("远程接口调用错误：",e);
            return null;
        }
        return obj;
    }
}
