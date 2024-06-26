package com.example.bigevent.controller;

import com.example.bigevent.mapper.UserMapper;
import com.example.bigevent.pojo.Result;
import com.example.bigevent.pojo.User;
import com.example.bigevent.service.UserService;
import com.example.bigevent.utils.JwtUtil;
import com.example.bigevent.utils.Md5Util;
import com.example.bigevent.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$")String password) {
        //查询用户
        User user = userService.findByUserName(username);
        if (user == null) {
            //注册用户
            userService.register(username,password);
            return Result.success();
        }else {
            return Result.error("用户名已被占用");
        }

    }
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$")String password) {
        //查询用户
        User user = userService.findByUserName(username);
        if(user == null) {
            return Result.error("用户名不存在");
        }else {
            if(user.getPassword().equals(Md5Util.getMD5String(password))) {
                Map<String,Object> claims = new HashMap<>();
                claims.put("id",user.getId());
                claims.put("username",user.getUsername());
                String token = JwtUtil.genToken(claims);
                return Result.success(token);
            }
        }
        return Result.error("密码错误");
    }
    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        Map<String,Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }
}
