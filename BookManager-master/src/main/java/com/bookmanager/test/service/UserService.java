package com.bookmanager.test.service;

import com.bookmanager.test.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {


    User login(String username, String password, Byte isAdmin);


    Integer register(String username, String password);


    void setPassword(Integer id, String password);

    List<User> queryUsersByPage(Integer page, Integer size);

    Integer getCount();

    Integer addUser(User user);

    Integer deleteUser(User user);

    Integer deleteUsers(List<User> users);

    Integer updateUser(User user);

    List<User> queryUsers();

    int getSearchCount(Map<String, Object> searchParam);

    List<User> searchUsersByPage(Integer page, Integer size, Map<String, Object> searchParam);
}
