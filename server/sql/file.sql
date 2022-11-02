USE file_cloud;

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
   `fileid` int NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '文件id',
   `userid` varchar(36) NOT NULL COMMENT '用户id',
   `filepath` varchar(50) NOT NULL COMMENT '文件路径',
   `is_file` tinyint NOT NULL COMMENT '是否为文件',
   `md5` varchar(32) COMMENT '文件md5值',
   `create_time` datetime COMMENT '创建时间',
   `modified_time` datetime COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
select * from `file`;