package com.zhangyun.filecloud.server.database.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zy
 * @since 2022-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FileChangeRecord对象", description="")
public class FileChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文件相对路径")
    private String relativePath;

    @ApiModelProperty(value = "文件类型：文件/目录")
    private Integer fileType;

    @ApiModelProperty(value = "操作类型：创建/修改/删除")
    private Integer operationType;

    @ApiModelProperty(value = "设备号")
    private String deviceId;

    @ApiModelProperty(value = "传输模式")
    private Integer transferMode;

    @ApiModelProperty(value = "传输状态：完成/加载中")
    private Integer status;

    @ApiModelProperty(value = "起始传输位置")
    private Long startPos;

    @ApiModelProperty(value = "消息体最大长度")
    private Long maxReadLength;

    @ApiModelProperty(value = "逻辑删除字段")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifiedTime;


}
