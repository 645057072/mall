package com.hmall.dao;


import com.hmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkByUsername(String username);

    int checkByEimal(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByusername(String username);

    int checkAnswer(@Param("username")String username, @Param("question")String question,@Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkPassword(@Param("password")String password,@Param("userid") Integer userid);

    int checkEmialByUserid(@Param("email")String email,@Param("userid") Integer userid);
}