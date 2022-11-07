package com.zhangyun.filecloud.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.filecloud.server.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
public interface IUserService extends IService<User> {
    User selectUserByName(String username);

    boolean createUser(String username, String password);

    boolean deleteUser(String username, String password);

    boolean updateUser(User user);

    Integer getIdByUsername(String username);
}
