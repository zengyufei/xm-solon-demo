create database xunmo;
use xunmo;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for author
-- ----------------------------
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `first_name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `last_name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `modified_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `business_key_author`(`first_name`, `last_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of author
-- ----------------------------
INSERT INTO `author` VALUES (1, 'Eve', 'Procello', 'F', '2023-06-26 10:14:43', NULL);
INSERT INTO `author` VALUES (2, 'Alex', 'Banks', 'M', '2023-06-26 10:14:43', NULL);
INSERT INTO `author` VALUES (3, 'Dan', 'Vanderkam', 'M', '2023-06-26 10:14:43', NULL);
INSERT INTO `author` VALUES (4, 'Boris', 'Cherny', 'M', '2023-06-26 10:14:43', NULL);
INSERT INTO `author` VALUES (5, 'Samer', 'Buna', 'M', '2023-06-26 10:14:43', NULL);

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `edition` int(0) NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `store_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `business_key_book`(`name`, `edition`) USING BTREE,
  INDEX `fk_book__book_store`(`store_id`) USING BTREE,
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`store_id`) REFERENCES `book_store` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (1, 'Learning GraphQL', 1, 50.00, 1);
INSERT INTO `book` VALUES (2, 'Learning GraphQL', 2, 55.00, 1);
INSERT INTO `book` VALUES (3, 'Learning GraphQL', 3, 51.00, 1);
INSERT INTO `book` VALUES (4, 'Effective TypeScript', 1, 73.00, 1);
INSERT INTO `book` VALUES (5, 'Effective TypeScript', 2, 69.00, 1);
INSERT INTO `book` VALUES (6, 'Effective TypeScript', 3, 88.00, 1);
INSERT INTO `book` VALUES (7, 'Programming TypeScript', 1, 47.50, 1);
INSERT INTO `book` VALUES (8, 'Programming TypeScript', 2, 45.00, 1);
INSERT INTO `book` VALUES (9, 'Programming TypeScript', 3, 48.00, 1);
INSERT INTO `book` VALUES (10, 'GraphQL in Action', 1, 80.00, 2);
INSERT INTO `book` VALUES (11, 'GraphQL in Action', 2, 81.00, 2);
INSERT INTO `book` VALUES (12, 'GraphQL in Action', 3, 80.00, 2);

-- ----------------------------
-- Table structure for book_author_mapping
-- ----------------------------
DROP TABLE IF EXISTS `book_author_mapping`;
CREATE TABLE `book_author_mapping`  (
  `book_id` bigint(0) UNSIGNED NOT NULL,
  `author_id` bigint(0) UNSIGNED NOT NULL,
  PRIMARY KEY (`book_id`, `author_id`) USING BTREE,
  INDEX `fk_book_author_mapping__author`(`author_id`) USING BTREE,
  CONSTRAINT `book_author_mapping_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `book_author_mapping_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_author_mapping
-- ----------------------------
INSERT INTO `book_author_mapping` VALUES (1, 1);
INSERT INTO `book_author_mapping` VALUES (2, 1);
INSERT INTO `book_author_mapping` VALUES (3, 1);
INSERT INTO `book_author_mapping` VALUES (1, 2);
INSERT INTO `book_author_mapping` VALUES (2, 2);
INSERT INTO `book_author_mapping` VALUES (3, 2);
INSERT INTO `book_author_mapping` VALUES (4, 3);
INSERT INTO `book_author_mapping` VALUES (5, 3);
INSERT INTO `book_author_mapping` VALUES (6, 3);
INSERT INTO `book_author_mapping` VALUES (7, 4);
INSERT INTO `book_author_mapping` VALUES (8, 4);
INSERT INTO `book_author_mapping` VALUES (9, 4);
INSERT INTO `book_author_mapping` VALUES (10, 5);
INSERT INTO `book_author_mapping` VALUES (11, 5);
INSERT INTO `book_author_mapping` VALUES (12, 5);

-- ----------------------------
-- Table structure for book_store
-- ----------------------------
DROP TABLE IF EXISTS `book_store`;
CREATE TABLE `book_store`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `business_key_book_store`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_store
-- ----------------------------
INSERT INTO `book_store` VALUES (1, 'O\'REILLY', NULL);
INSERT INTO `book_store` VALUES (2, 'MANNING', NULL);

-- ----------------------------
-- Table structure for tree_node
-- ----------------------------
DROP TABLE IF EXISTS `tree_node`;
CREATE TABLE `tree_node`  (
  `node_id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parent_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`node_id`) USING BTREE,
  UNIQUE INDEX `business_key_tree_node`(`parent_id`, `name`) USING BTREE,
  CONSTRAINT `tree_node_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `tree_node` (`node_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tree_node
-- ----------------------------
INSERT INTO `tree_node` VALUES (1, 'Home', NULL);
INSERT INTO `tree_node` VALUES (9, 'Clothing', 1);
INSERT INTO `tree_node` VALUES (2, 'Food', 1);
INSERT INTO `tree_node` VALUES (6, 'Bread', 2);
INSERT INTO `tree_node` VALUES (3, 'Drinks', 2);
INSERT INTO `tree_node` VALUES (4, 'Coca Cola', 3);
INSERT INTO `tree_node` VALUES (5, 'Fanta', 3);
INSERT INTO `tree_node` VALUES (7, 'Baguette', 6);
INSERT INTO `tree_node` VALUES (8, 'Ciabatta', 6);
INSERT INTO `tree_node` VALUES (18, 'Man', 9);
INSERT INTO `tree_node` VALUES (10, 'Woman', 9);
INSERT INTO `tree_node` VALUES (11, 'Casual wear', 10);
INSERT INTO `tree_node` VALUES (15, 'Formal wear', 10);
INSERT INTO `tree_node` VALUES (12, 'Dress', 11);
INSERT INTO `tree_node` VALUES (14, 'Jeans', 11);
INSERT INTO `tree_node` VALUES (13, 'Miniskirt', 11);
INSERT INTO `tree_node` VALUES (17, 'Shirt', 15);
INSERT INTO `tree_node` VALUES (16, 'Suit', 15);
INSERT INTO `tree_node` VALUES (19, 'Casual wear', 18);
INSERT INTO `tree_node` VALUES (22, 'Formal wear', 18);
INSERT INTO `tree_node` VALUES (20, 'Jacket', 19);
INSERT INTO `tree_node` VALUES (21, 'Jeans', 19);
INSERT INTO `tree_node` VALUES (24, 'Shirt', 22);
INSERT INTO `tree_node` VALUES (23, 'Suit', 22);

SET FOREIGN_KEY_CHECKS = 1;
