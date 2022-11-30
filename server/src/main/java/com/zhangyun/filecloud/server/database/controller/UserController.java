package com.zhangyun.filecloud.server.database.controller;


import com.zhangyun.filecloud.server.database.entity.User;
import com.zhangyun.filecloud.server.database.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/selectUserByName")
    public User selectUserByName(@RequestParam String username) {
        return userService.selectUserByName(username);
    }

    @GetMapping("/createUser")
    public boolean createUser(@RequestParam String username,
                              @RequestParam String password) {
        return userService.createUser(username, password);
    }

    @GetMapping("/deleteUser")
    public boolean deleteUser(@RequestParam String username,
                              @RequestParam String password) {
        return userService.deleteUser(username, password);
    }

    @PostMapping("/updateUser")
    public boolean updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

}
