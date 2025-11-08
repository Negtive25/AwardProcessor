CREATE DATABASE award

USE award

DROP TABLE student
CREATE TABLE `student` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `studentId` VARCHAR(20) NOT NULL,
                           `password` VARCHAR(100) NOT NULL,
                           `name` VARCHAR(50) COMMENT '学生姓名',
                           `college` VARCHAR(100) COMMENT '学院',
                           `major` VARCHAR(100) COMMENT '专业',
                           `auth` VARCHAR(50) NOT NULL DEFAULT 'student',
                           `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `idxStudentId` (`studentId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生信息表';

DROP TABLE standard_award
CREATE TABLE `standard_award` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `standardAwardId` VARCHAR(256) NOT NULL,
                                  `name` VARCHAR(255) NOT NULL COMMENT '标准奖项名称，如“全国大学生数学建模竞赛一等奖”',
                                  `category` VARCHAR(100) DEFAULT '其他' COMMENT '奖项类别，如“学科竞赛”、“学术论文”、“社会实践”等',
                                  `level` VARCHAR(50) DEFAULT NULL COMMENT '奖项级别，如“国家级”、“省级”、“校级”',
                                  `score` DOUBLE NOT NULL DEFAULT 0.00 COMMENT '此奖项对应的分数',
                                  `isActive` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用 (可用于软删除或临时禁用某个奖项)',
                                  `createdBy` VARCHAR(50) COMMENT '创建此奖项的管理员ID',
                                  `updatedBy` VARCHAR(50) COMMENT '最后修改此奖项的管理员ID',
                                  `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idxStandardAwardId`(standardAwardId),
                                  KEY `idxName` (`name`),
                                  KEY `fkStandardAwardsCreatedBy` (`createdBy`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准奖项库表';

DROP TABLE award_submission
CREATE TABLE `award_submission` (
                                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `submissionId` VARCHAR(256) NOT NULL,
                                    `studentId`VARCHAR(20) NOT NULL,
                                    `imageObjectKey` VARCHAR(512) NOT NULL COMMENT '奖状图片在对象存储中的ObjectKey',
                                    `status` ENUM(
                                        'AI_PROCESSING',       -- 1. 处理中 (任务已入队，等待AI处理)
                                        'AI_APPROVED',         -- 4. 已通过 (AI审核通过,等待管理员复审)
                                        'AI_REJECTED',          -- 5. 已驳回 (AI驳回,等待管理员复审)
                                        'MANUAL_APPROVED',
                                        'MANUAL_REJECTED',
                                        'ERROR_NEED_TO_MANUAL_REVIEW'
                                        ) DEFAULT 'AI_PROCESSING' COMMENT '奖状处理状态',
                                    `ocrFullText` JSON COMMENT 'OCR识别出的原始全文',
                                    `matchedAwardId` VARCHAR(256) COMMENT '最终匹配上的标准奖项ID',
                                    `finalScore` DOUBLE COMMENT '最终获得的分数',
                                    `reason` VARCHAR(500) COMMENT '同意或驳回原因 (AI或管理员填写)',
                                    `suggestion` JSON COMMENT 'AI辅助信息，如ES检索的Top5候选列表，供管理员参考',
                                    `duplicateCheckResult` ENUM('NOT_CHECKED', 'IS_DUPLICATE', 'NOT_DUPLICATE') DEFAULT 'NOT_CHECKED' COMMENT 'LLM查重结果',
                                    `reviewedBy`  VARCHAR(50) COMMENT '进行人工审核的管理员用户名',
                                    `submittedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '学生提交时间',
                                    `completedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录每次更新提交状态的时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY (`submissionId`),
                                    KEY (`studentId`, `status`),
                                    KEY (`status`),
                                    KEY (`matchedAwardId`),
                                    KEY (`reviewedBy`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生奖状提交及处理记录表';

DROP TABLE `admin`
CREATE TABLE `admin` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                         `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员登录用户名',
                         `password` VARCHAR(255) NOT NULL COMMENT '哈希后的登录密码',
                         `fullName` VARCHAR(100) COMMENT '管理员姓名',
                         `auth` VARCHAR(50) NOT NULL DEFAULT 'admin' COMMENT '角色 (可扩展，如 admin, superadmin)',
                         `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员信息表';