package com.zhangyun.filecloud.server.database.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.server.config.Config;
import com.zhangyun.filecloud.server.database.entity.User;
import com.zhangyun.filecloud.server.database.mapper.UserMapper;
import com.zhangyun.filecloud.server.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @TraceLog
    public User selectUserByName(String username) {
        return userMapper.selectUserByName(username);
    }

    @Override
    @TraceLog
    public boolean createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtil.md5Hex(password));
        // 插入数据库
        Integer insert = userMapper.insert(user);
        return insert > 0;
    }

    @Override
    @TraceLog
    public boolean deleteUser(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        int delete = userMapper.delete(wrapper);
        return delete > 0;
    }

    @Override
    @TraceLog
    public boolean updateUser(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        int update = userMapper.update(user, wrapper);
        return update > 0;
    }

    @Override
    public Integer getIdByUsername(String username) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        List<User> users = userMapper.selectByMap(map);
        return users.get(0).getUserId();
    }

    @Override
    public String getUserRootPath(String username) {
        return Paths.get(Config.ROOT_PATH, username).toString();
    }
}
