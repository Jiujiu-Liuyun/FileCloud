USE file_cloud;

DROP TABLE IF EXISTS `file_change_record`;
CREATE TABLE `file_change_record` (
   `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键id',
   `relative_path` varchar(10240) NOT NULL COMMENT '文件相对路径',
   `file_type` int NOT NULL COMMENT '文件类型：文件/目录',
   `operation_type` int NOT NULL COMMENT '操作类型：创建/修改/删除',
   `device_id` varchar(36) NOT NULL COMMENT '设备号',
   `transfer_mode` int NOT NULL comment '传输模式',
   `status` int NOT NULL comment '传输状态：完成/加载中',
   `start_pos` bigint COMMENT '起始传输位置',
   `max_read_length` bigint COMMENT '消息体最大长度',
   `deleted` int default(0) comment '逻辑删除字段',
   `create_time` datetime COMMENT '创建时间',
   `modified_time` datetime COMMENT '修改时间',
   index file_change_record(device_id, relative_path(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

select * from `file_change_record`;
