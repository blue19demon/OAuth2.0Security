

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for um_t_app_info
-- ----------------------------
DROP TABLE IF EXISTS `um_t_app_info`;
CREATE TABLE `um_t_app_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(255) DEFAULT NULL,
  `app_name` varchar(255) DEFAULT NULL,
  `app_icon` varchar(255) NOT NULL,
  `app_secret` varchar(255) NOT NULL,
  `created_time` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of um_t_app_info
-- ----------------------------
INSERT INTO `um_t_app_info` VALUES ('1', 'OaH1heR2E4eGnBr87Br8F', '汽车之家', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571887636&di=31eaa24349d9131bd121541d72cbfe14&imgtype=jpg&er=1&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171124%2Ff2c45eb98a4d4f9b9ec37018124c8085.jpeg', 'gY/Hauph1tqvVWiH4atxteSH8sRX03IDXRIQi03DVTFGzKfz8ZtGi', null, null);
INSERT INTO `um_t_app_info` VALUES ('2', 'gY/Hauph1tqvVWiH4atxte', '同步推', 'https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=161db13674c6a7efb926af20c5c1c86c/8ad4b31c8701a18b76da06a69c2f07082838fe22.jpg', 'OaH1heR2E4eGnBr87Br8FgY/Hauph1tqvVWiH4atxtegY/Hauph', null, null);

-- ----------------------------
-- Table structure for um_t_role
-- ----------------------------
DROP TABLE IF EXISTS `um_t_role`;
CREATE TABLE `um_t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `created_time` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of um_t_role
-- ----------------------------
INSERT INTO `um_t_role` VALUES ('1', '管理员拥有所有接口操作权限', '1571120050', '管理员', 'ADMIN');
INSERT INTO `um_t_role` VALUES ('2', '普通拥有查看用户列表与修改密码权限，不具备对用户增删改权限', '1571120050', '普通用户', 'USER');

-- ----------------------------
-- Table structure for um_t_role_user
-- ----------------------------
DROP TABLE IF EXISTS `um_t_role_user`;
CREATE TABLE `um_t_role_user` (
  `role_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  UNIQUE KEY `UK_o2eh9w9ha9qh5557mfyw1gxv1` (`user_id`),
  KEY `FK1pk6e2w79ro1xo8uus7q32pnf` (`role_id`),
  CONSTRAINT `FK1pk6e2w79ro1xo8uus7q32pnf` FOREIGN KEY (`role_id`) REFERENCES `um_t_role` (`id`),
  CONSTRAINT `FKh2d913l31o8vjsgpfkh10bkyw` FOREIGN KEY (`user_id`) REFERENCES `um_t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of um_t_role_user
-- ----------------------------
INSERT INTO `um_t_role_user` VALUES ('1', '1');
INSERT INTO `um_t_role_user` VALUES ('2', '2');

-- ----------------------------
-- Table structure for um_t_user
-- ----------------------------
DROP TABLE IF EXISTS `um_t_user`;
CREATE TABLE `um_t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL COMMENT '昵称',
  `gender` varchar(255) DEFAULT NULL,
  `head_image_url` varchar(255) DEFAULT NULL,
  `created_time` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of um_t_user
-- ----------------------------
INSERT INTO `um_t_user` VALUES ('1', 'admin', '123456', '小小丰', '男', 'http://thirdwx.qlogo.cn/mmopen/vi_32/V70pm8vC3fbkBXHKhcvMVWGPePN93PvRhINMNw7MCmU0r3lrzde78JUZBicvBibTt7Vvyshgw4tRUzYoiaZicdia2Pg/132', null, null);
INSERT INTO `um_t_user` VALUES ('2', 'xiaohong', '123456', 'blue demo', 'W', 'http://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaELD6e1lnDCatw9xWUakbUeqO4kQ9xRhGF0GnKhMhICyaUaSgGcvRPtsVM9LqadB3NBwb0I9rsuRiaw/132\r\n', '1571121217465', null);
