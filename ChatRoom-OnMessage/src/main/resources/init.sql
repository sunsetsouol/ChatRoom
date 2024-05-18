drop
    database if exists chatroom;
create
    database chatroom;

use
    chatroom;

DROP TABLE IF EXISTS `t_room`;
CREATE TABLE `t_room`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
    `room_name`    varchar(255) NOT NULL COMMENT '聊天室名字',
    `description`  varchar(255)          DEFAULT NULL COMMENT '描述',
    `avatar`       varchar(255) NOT NULL DEFAULT 'https://sunsetsouol.oss-cn-guangzhou.aliyuncs.com/pic/202405171011634.png' COMMENT '头像',
    `member_limit` int(5)       NOT NULL DEFAULT 200 COMMENT '成员人数上限',
    `owner_id`     bigint(20)   NOT NULL COMMENT '群主id',
    `member_count` int(11)      NOT NULL DEFAULT 1 COMMENT '群聊人数',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      int(11)               DEFAULT 0 COMMENT '逻辑删除',
    CHECK ( `member_count` < `member_limit` ),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `t_user_room`;
CREATE TABLE `t_user_room`
(
    `id`          bigint(20) NOT NULL,
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `room_id`     bigint(20) NOT NULL COMMENT '房间id',
    `identity`    int(11)    NOT NULL DEFAULT 0 COMMENT '身份（0普通成员，1管理员，2群主，-1退出群聊）',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     int(11)             DEFAULT 0 COMMENT '逻辑删除',
    CHECK ( `identity` IN (0, 1, 2, -1) ),
    UNIQUE (`user_id`, `room_id`, `deleted`),
    INDEX (`room_id`, `user_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;