package com.zhangyun.filecloud.server.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
public class File extends Model<File> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    /**
     * 用户id
     */
    @TableField("userid")
    private String userId;

    /**
     * 文件路径
     */
    @TableField("filepath")
    private String filePath;

    /**
     * 是否为文件
     */
    @TableField("is_file")
    private Integer isFile;

    /**
     * 文件md5值
     */
    private String md5;

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

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public Integer getIsFile() {
        return isFile;
    }

    public void setIsFile(Integer isFile) {
        this.isFile = isFile;
    }
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }

    @Override
    public String toString() {
        return "File{" +
        "fileid=" + fileId +
        ", userid=" + userId +
        ", filepath=" + filePath +
        ", isFile=" + isFile +
        ", md5=" + md5 +
        ", createTime=" + createTime +
        ", modifiedTime=" + modifiedTime +
        "}";
    }
}
