package com.bookmanager.test.web;

import com.bookmanager.test.model.User;
import com.bookmanager.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    // 登錄
    @RequestMapping(value = "/login")
    public Integer login(String username, String password, String authority, HttpServletRequest req){
        // res：0 賬號或密碼錯誤，1 登錄成功
        byte isAdmin = (byte) (authority.equals("manager") ? 1 : 0);
        User user = userService.login(username, password, isAdmin);
        if(user == null) return 0;
        // 存session
        req.getSession().setAttribute("userObj", user);
        return 1;
    }

    // 注冊
    @RequestMapping(value = "/register")
    public Integer register(String username, String password){
        // res：0 用戶名重復，1 注冊成功
        return userService.register(username, password);
    }

    // 取得用戶
    @RequestMapping(value = {
            "/getUser",
            "/reader/getUser"
    })
    public User getUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("userObj");
    }

    // 登出
    @RequestMapping(value = {
            "/exitLogin",
            "/reader/exitLogin"
    })
    public void exitLogin(HttpServletRequest req){
        // clean session
        req.getSession().removeAttribute("userObj");
    }

    // 密碼修改
    @RequestMapping(value = {
            "/alterPassword",
            "reader/alterPassword"
    })
    public Integer alterPassword(String oldPassword, String newPassword,HttpServletRequest req){
        //獲取當前賬號信息
        User userObj =  (User) req.getSession().getAttribute("userObj");

        //檢查舊密碼是否正確
        User user = userService.login(userObj.getUsername(), oldPassword, userObj.getIsadmin());
        if(user == null) {  //舊密碼不正確
            return 0;
        } else {    //舊密碼正確，設置新密碼
            userService.setPassword(userObj.getUserid(), newPassword);
            return 1;
        }

    }

    // 查詢用戶
    @GetMapping(value = "/queryUsersByPage")
    public Map<String, Object> queryUsersByPage(Integer page, Integer limit){
        // 取得數量
        Integer count = userService.getCount();

        //取得數據
        List<User> users = userService.queryUsersByPage(page, limit);

        // 結果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", users);
        return res;
    }

    // 查詢所有用戶
    @GetMapping(value = "/queryUsers")
    public List<User> queryUsers(){
        return userService.queryUsers();
    }

    // 新增用戶
    @PostMapping(value = "/addUser")
    public Integer addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    // 取得數量
    @GetMapping(value = "/getCount")
    public Integer getCount(){
        return userService.getCount();
    }

    // 刪除用戶
    @DeleteMapping(value = "/deleteUser")
    public Integer deleteUser(@RequestBody User user){
        return userService.deleteUser(user);
    }

    // 刪除部分用戶
    @DeleteMapping(value = "/deleteUsers")
    public Integer deleteUsers(@RequestBody List<User> users){
        return userService.deleteUsers(users);
    }

    // 更新用戶
    @RequestMapping(value = "/updateUser")
    public Integer updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    // 搜索用戶
    @GetMapping("/searchUsersByPage")
    public Map<String,Object> searchBookTypesByPage(Integer page, Integer limit, String json){
        //搜索的參數
        Map<String, Object> searchParam = JsonUtil.parseMap(json, String.class, Object.class);
        //查詢個數
        int count = userService.getSearchCount(searchParam);
        //查詢數據
        List<User> users = userService.searchUsersByPage(page, limit, searchParam);
        //結果map
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("code", 0);
        res.put("msg", "success");
        res.put("count", count);
        res.put("data", users);
        return res;
    }
}
