USE file_cloud;

DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
   `device_id` varchar(36) PRIMARY KEY COMMENT '设备id',
   `user_id` int NOT NULL COMMENT '用户id',
   `device_name` varchar(50) COMMENT '设备名',
   `create_time` datetime COMMENT '创建时间',
   `modified_time` datetime COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SELECT * FROM `device`;
