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
 * @since 2022-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="File对象", description="")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件id")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "相对路径")
    private String relativePath;

    @ApiModelProperty(value = "文件类型")
    private Integer fileType;

    @ApiModelProperty(value = "该文件在服务器是最新的")
    private Integer serverIsLatest;

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
