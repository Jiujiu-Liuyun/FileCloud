package com.zhangyun.filecloud.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyun.filecloud.server.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
public interface UserMapper extends BaseMapper<User> {
    User selectUserByName(@Param("name") String name);
}
