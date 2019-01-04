package com.hmall.unit;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertieUitl {

    private static Logger logger= LoggerFactory.getLogger(PropertieUitl.class);

    private static Properties properties;

    static {
        String fileName="hmall.properties";
        properties=new Properties();
        try {
            properties.load(new InputStreamReader(PropertieUitl.class.getClassLoader().
                    getResourceAsStream(fileName),"UTF-8"));
        }catch (IOException e){
         logger.error("未读取配置文件",e);
        }
    }
    public static String getProperty(String key){
        String value=properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultvalue){
        String value=properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value=defaultvalue;
        }
        return value.trim();
    }
}
