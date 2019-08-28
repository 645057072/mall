package com.hmall.service.Impl;

import com.hmall.common.Const;
import com.hmall.common.ServiceResponse;
import com.hmall.dao.UserMapper;
import com.hmall.pojo.User;
import com.hmall.service.IUserService;
import com.hmall.unit.MD5Uitl;
import com.hmall.unit.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Slf4j
@Service("iUserService")
public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    //用户登录
    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkByUsername(username);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }

        String md5password=MD5Uitl.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5password);
        if (user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServiceResponse.createBySuccess("登录成功",user);
    }

    @Override

    //用户注册
    public ServiceResponse<String> register(User user){

        ServiceResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSucess()){
            return validResponse;
        }
         validResponse=this.checkValid(user.getEmail(),Const.EMIAL);
        if(!validResponse.isSucess()){
            return  validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Uitl.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        if (resultCount==0){
                return ServiceResponse.createByErrorMessage("参数错误");
            }
            return ServiceResponse.createBySuccessMessage("注册成功");
    }

    //用户和邮箱检验
    public ServiceResponse<String> checkValid(String str, String type){
        if (StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkByUsername(str);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("用户已存在");
                }
            }
            if(Const.EMIAL.equals(type)){
                int resultCount=userMapper.checkByEimal(str);
                if (resultCount>0){
                    return  ServiceResponse.createByErrorMessage("Emial已存在");
                }
            }
        }else {
            return ServiceResponse.createBySuccessMessage("校验失败");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }
    //查找问题
    public ServiceResponse<String> selectquestion(String username){
        ServiceResponse validResponse=this.checkValid(username,Const.CURRENT_USER);
        if(validResponse.isSucess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.selectQuestionByusername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }
    //查找答案
    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
          String forgetToken= UUID.randomUUID().toString();
//            设置TOKEN过期时间12个小时
            RedisShardedPoolUtil.setex(forgetToken,Const.TOKEN_PREFIX+username,60*60*12);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题答案错误");
    }

    public  ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误，传递token失败");
        }
        ServiceResponse validResponse=this.checkValid(username,Const.CURRENT_USER);
        if(validResponse.isSucess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
//
        String token= RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return  ServiceResponse.createByErrorMessage("token失效");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Uitl.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServiceResponse.createByErrorMessage("获取token失效，请重置token系统参数!");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }
//修改用户密码
    public ServiceResponse<String> restPassword(String passwordOld,String passwordNew,User user){
        int resultCount=userMapper.checkPassword(MD5Uitl.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Uitl.MD5EncodeUtf8(passwordNew));
        int setCount=userMapper.updateByPrimaryKeySelective(user);
        if(setCount>0){
            return ServiceResponse.createBySuccessMessage("密码修改成功");
        }
        return ServiceResponse.createByErrorMessage("密码更新失败");
    }

    public ServiceResponse<User> updateInfoMation(User user){
        //用户不能被更新
        //email也需要进行一个校验，校验新的email是不是已经存在，并且存在email的话，不能是我们这个用户的
        int resultCount=userMapper.checkEmialByUserid(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("邮箱已存在，更新失败");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);

        if (updateCount>0){
            return ServiceResponse.createBySuccess("更新信息成功",updateUser);
        }
        return ServiceResponse.createBySuccessMessage("更新个人信息失败");
    }

    public ServiceResponse<User> getInfomation(Integer userid){
        User user=userMapper.selectByPrimaryKey(userid);
        if(user==null){
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccessMessage("用户登录成功");
    }

    public ServiceResponse checkAdmin(User user){
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }
}
