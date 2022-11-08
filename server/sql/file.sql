USE file_cloud;

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
   `file_id` int NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '文件id',
   `user_id` varchar(36) NOT NULL COMMENT '用户id',
   `file_path` varchar(50) NOT NULL COMMENT '文件路径',
   `is_file` tinyint NOT NULL COMMENT '是否为文件',
   `md5` varchar(32) COMMENT '文件md5值',
   `devices_need_to_update` varchar(1024) comment '需要更新的设备',
   `create_time` datetime COMMENT '创建时间',
   `modified_time` datetime COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
select * from `file`;