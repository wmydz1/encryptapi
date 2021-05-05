DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cipherText` longtext,
  `path` varchar(255) DEFAULT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `keyStoreText` text,
  `createTime` bigint(20) DEFAULT NULL,
  `enable` int(2) NOT NULL DEFAULT '1',
  `expire` int(11) DEFAULT '0',
  `secondPassword` varchar(255) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path-index` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
