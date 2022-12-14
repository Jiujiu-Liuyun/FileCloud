package com.zhangyun.filecloud.server.database.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    @TableId("device_id")
    private String deviceId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 设备名
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 用户根路径
     */
    @TableField("root_path")
    private String rootPath;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private Date modifiedTime;

}
