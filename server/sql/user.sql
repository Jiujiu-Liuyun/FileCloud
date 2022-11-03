USE file_cloud;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
   `user_id` int NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
   `username` varchar(20) NOT NULL unique COMMENT '用户名',
   `password` varchar(50) NOT NULL COMMENT '用户密码',
   `create_time` timestamp COMMENT '创建时间',
   `modified_time` timestamp COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SELECT * from `user`;

update `user`
set `user_id` = 1
where `username` = 'zhangyun';

SET SQL_SAFE_UPDATES = 0;

# insert into user(username, password) values('zhangyun', '1120'); 