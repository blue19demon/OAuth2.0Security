SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;
/**
初始化角色信息
 */
 CREATE TABLE IF NOT EXISTS `um_t_role`(
`id` INT(11) PRIMARY KEY AUTO_INCREMENT ,
 `description` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `created_time` BIGINT(20) NOT NULL,
 `name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
 `role` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
);
INSERT IGNORE INTO `um_t_role`(id,`name`,description,created_time,role) VALUES(1,'管理员','管理员拥有所有接口操作权限',UNIX_TIMESTAMP(NOW()),'ADMIN'),(2,'普通用户','普通拥有查看用户列表与修改密码权限，不具备对用户增删改权限',UNIX_TIMESTAMP(NOW()),'USER');

/**
初始化一个默认管理员
 */
 CREATE TABLE IF NOT EXISTS `um_t_user`(
`id` INT(11) PRIMARY KEY AUTO_INCREMENT ,
 `account` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `description` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `password` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
);
INSERT IGNORE INTO `um_t_user`(id,account,`password`,`name`,description) VALUES(1,'admin','123456','小小丰','系统默认管理员');

/**
关联表赋值 用户-角色
 */
CREATE TABLE IF NOT EXISTS `um_t_role_user`(
`role_id` INT(11),
 `user_id` INT(11)
);
INSERT IGNORE INTO `um_t_role_user`(role_id,user_id)VALUES(1,1);

/**
权限表
 */
 CREATE TABLE IF NOT EXISTS `um_t_permission`(
 `id` INT(11) PRIMARY KEY AUTO_INCREMENT ,
 `name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `description` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `url` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
 `pid` INT(11) DEFAULT NULL
);
INSERT INTO `um_t_permission` VALUES ('1', 'ROLE_HOME', 'home', '/', null), ('2', 'ROLE_ADMIN', 'ABel', '/admin', null);

/**
关联表赋值 角色-权限
 */
CREATE TABLE IF NOT EXISTS `um_t_permission_role`(
`role_id` INT(11),
`permission_id` INT(11)
);
INSERT INTO `um_t_permission_role` VALUES ('1', '1'), ('1', '2'), ('2', '1');
