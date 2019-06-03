package com.hmall.unit;

import com.hmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();
    static {
        //对象的字段需要全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
//        取消默认转换timestamps格式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
//        取消转换beans为空的类型
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
//      转换Date格式默认为"yyyy-mm-dd HH:MM:SS
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDRAD_FORMAT));
//        忽略在json中存在，在java中不存在的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 对象转换成String
     * @param obj
     * @param <T>
     * @return
     */

    public static <T> String obj2String(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj: objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    /**
     * 对象转换成String 并格式化
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj: objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    /**
     * String 转换成OBJECT对象，并返回泛型的对象
     * @param str
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T string2Object(String str,Class<T> tClass){
        if (StringUtils.isEmpty(str)||tClass==null){
            return null;
        }
        try {
            return tClass.equals(String.class)? (T)str: objectMapper.readValue(str,tClass);
        }catch (Exception e){
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T string2Object(String str, TypeReference<T> tTypeReference){
        if (StringUtils.isEmpty(str)||tTypeReference==null){
            return null;
        }
        try {
            return (T)(tTypeReference.getType().equals(String.class)? str: objectMapper.readValue(str,tTypeReference));
        }catch (Exception e){
            log.error("Parse String to Object error",e);
            return null;
        }
    }


    public static <T> T string2Object(String str,Class<?> CollectionClass,Class<?>...elementClasses){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(CollectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        }catch (Exception e){
            log.error("Parse String to Object error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user1=new User();
        user1.setId(3001);
        user1.setEmail("645057072@qq.com");


        User user2=new User();
        user2.setId(3002);
        user2.setEmail("35057072@qq.com");

        String userstr=JsonUtil.obj2StringPretty(user1);

        System.out.println(userstr);

        System.out.println("1-----------------------------------");

        List<User> userList=new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        String userListStr=JsonUtil.obj2StringPretty(userList);
        System.out.println(userListStr);


        System.out.println("2-----------------------------------");
        List<User> userList1=JsonUtil.string2Object(userListStr, new TypeReference<List<User>>() {
        });
        System.out.println(userList1);

        System.out.println("3-----------------------------------");
        List<User> userList2=JsonUtil.string2Object(userListStr,List.class,User.class);
        System.out.println(userList2);
    }
}
