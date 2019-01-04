package com.hmall.service;


import com.hmall.common.ServiceResponse;
import com.hmall.pojo.User;

public interface IUserService{
    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str, String type);

    ServiceResponse<String> selectquestion(String username);

    ServiceResponse<String> checkAnswer(String username,String question,String answer);

    ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);

    ServiceResponse<String> restPassword(String passwordOld,String passwordNew,User user);

    ServiceResponse<User> updateInfoMation(User user);

    ServiceResponse<User> getInfomation(Integer userid);

    ServiceResponse checkAdmin(User user);

}
