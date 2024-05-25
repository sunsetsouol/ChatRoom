/*
 Navicat Premium Data Transfer

 Source Server         : 47.120.32.160_3306
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 47.120.32.160:3306
 Source Schema         : chatroom

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 02/05/2024 21:21:26
*/
drop
    database if exists chatroom;
create
    database chatroom;

use
    chatroom;

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `username`    varchar(255) NOT NULL COMMENT '用户名',
    `phone`       varchar(20)  NOT NULL COMMENT '手机号',
    `password`    varchar(255) NOT NULL COMMENT '密码',
    `avatar`      varchar(255) NOT NULL default 'https://sunsetsouol.oss-cn-guangzhou.aliyuncs.com/pic/202405030905272.jpg' COMMENT '头像',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     int(11) NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE (`phone`, `deleted`) USING BTREE,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL COMMENT '角色名称',
    `permissions` varchar(255) NOT NULL COMMENT '权限',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     int(11) NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE (`name`, `deleted`) USING BTREE,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL COMMENT '权限名称',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     int(11) NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE (`name`, `deleted`) USING BTREE,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `role_id`     bigint(20) NOT NULL COMMENT '角色id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     int(11) NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE (`user_id`, `role_id`, `deleted`) USING BTREE,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;