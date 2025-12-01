# AwardProcessor - 基于 AI 与 RAG 的智能化学生奖状归档审核系统

## 项目简介

**AwardProcessor** 是一个集成了 **AIGC (生成式人工智能)** 和 **RAG (检索增强生成)** 技术的后端系统，旨在解决高校学生奖状收集难、人工审核效率低、重复提交难以甄别的问题。

本项目构建了一套完整的 **"上传 - 异步处理 - 向量检索 - 大模型推理 - 持久化"** 的自动化流水线。通过引入阿里云 Qwen 模型和 Elasticsearch 向量搜索，实现了对非结构化图片数据的结构化提取、智能归类和语义查重。

-----

## 技术栈 (Tech Stack)

  * **核心框架**: Spring Boot 3.4.2, Java 17
  * **人工智能 & LLM**: LangChain4j, Alibaba DashScope (Qwen-VL 视觉模型, Qwen-Flash 语言模型, Text-Embedding-v4)
  * **搜索引擎 & 向量数据库**: Elasticsearch 9.0.1 (Dense Vector, KNN, BM25)
  * **消息队列**: RocketMQ (实现削峰填谷与异步解耦)
  * **缓存 & 鉴权**: Redis, Spring Security + JWT
  * **对象存储**: Aliyun OSS (分片上传, 预签名 URL)
  * **数据库**: MySQL 8.0, MyBatis
  * **工具库**: MapStruct (DTO 映射), Apache Tika (文件类型检测), Hibernate Validator (参数校验)

-----

## 核心亮点与难点 (Highlights)

### 1\. 基于 RAG (检索增强生成) 的智能审核流水线

传统 OCR 只能提取文字，无法理解含义。本项目实现了一套完整的 RAG 流程来处理奖状：

  * **多模态 OCR**: 使用 `Qwen-VL` 视觉模型从图片中提取学生姓名、奖项名称和日期，并包含日期格式清洗逻辑 (`AIResponseDateValidator`)，解决了 AI 幻觉导致的日期格式错误问题（如 `2020-00-00`）。
  * **混合检索 (Hybrid Search)**: 在 Elasticsearch 中实现了 **向量相似度搜索 (KNN)** 与 **关键词全文检索 (BM25)** 的融合,将向量搜索和文本搜索的结果进行加权合并，提升了标准奖项匹配的召回率和准确率。
  * **Agent 推理**: 使用 LangChain4j 定义了声明式 Agent (`ClassificationAgent`)，让 LLM 基于检索到的候选列表进行逻辑推理，判断奖状归属并输出严格的 JSON 格式。

### 2\. 深度语义查重机制

为了防止学生重复提交同一奖项（例如不同拍摄角度、不同年份的同名奖项）：

  * **逻辑分层**: 定义了"完美匹配"（日期+名称）和"核心匹配"（名称相似但日期缺失）两套策略。
  * **AI 判决**: 不仅仅是字符串比对，而是将新旧记录交给 LLM 进行语义分析，输出查重结论 (`DeduplicationResult`)，有效识别模糊和歧义情况。

### 3\. 高性能与高可用架构

  * **RocketMQ 异步解耦**: 图片上传后立即返回成功响应，将耗时的 AI 处理任务投递到 MQ。消费者端 (`StudentAwardSubmissionConsumer`) 实现了重试机制（Max 3次）和死信处理，确保任务不丢失，且防止 AI 服务波动阻塞主线程。
  * **分片上传与断点续传**: 实现了基于 Redis 的分片上传逻辑，支持大文件上传，并使用 Apache Tika 检测真实文件类型（防止扩展名伪造）。
  * **游标分页 (Cursor Pagination)**: 在 `BaseCursorPageService` 中实现了基于 `ULID` 的游标分页方案。相比传统的 `Offset-Limit` 分页，在海量数据下性能稳定且不会出现数据遗漏或重复。

### 4\.代码规范与安全性

  * **防御性编程**: 全局异常处理 (`GlobalExceptionHandler`)，自定义注解校验 (`@ValidPassword`, `@ValidEnum`, `@AtLeastOneIsValid`)。
  * **安全性**: 禁用传统 Session，构建基于 Redis 的可撤销 JWT 认证体系，实现对 Token 的服务端主动管控（如强制下线、即时封禁）；OSS 链接采用预签名 URL (Presigned URL) 且配合图片压缩参数，防止原始链接泄露和盗链，同时减少传输带宽。
  * **设计模式**: 广泛使用 Builder 模式构建对象，策略模式处理不同的登录类型，模板方法模式处理分页逻辑。

-----

## 业务运行逻辑 (Workflow)

### 1\. 学生提交奖状

1.  **初始化上传**: 客户端请求上传，服务器生成 `UploadId` 并存入 Redis。
2.  **分片上传**: 客户端并发上传文件分片，服务器校验分片大小与类型，存入 OSS。
3.  **合并与生产**: 上传完成后，合并分片，生成唯一的 `SubmissionId` (ULID)，并将任务消息发送至 **RocketMQ** 的 `submissions` Topic。

### 2\. AI 异步处理 (核心消费者逻辑)

`StudentAwardSubmissionConsumer` 监听消息队列，执行以下步骤：

1.  **OCR 识别**: 调用 Qwen-VL 视觉大模型，识别图片中的文字信息。
2.  **格式清洗**: 校验并修复 OCR 返回的日期格式（处理 `00` 月 `00` 日等异常）。
3.  **标准库检索**: 将识别出的"奖项名称"向量化，在 Elasticsearch 中进行混合检索，获取 Top 5 相似的标准奖项。
4.  **LLM 归类**: 将 OCR 结果与 Top 5 候选奖项投喂给 `ClassificationAgent`，AI 判断是否匹配，并给出置信理由。
5.  **语义查重**: 如果归类成功，拉取该学生历史已通过的奖项，再次投喂给 AI 进行查重比对。
6.  **结果落库**: 根据 AI 的判断结果，更新数据库状态为 `AI_APPROVED` (通过)、`AI_REJECTED` (驳回) 或 `IS_DUPLICATE` (重复)，或者在异常时转为人工审核 `ERROR_NEED_TO_MANUAL_REVIEW`。

### 3\. 管理员管理

  * **标准库维护**: 管理员增删改查标准奖项，系统自动同步更新 MySQL 和 Elasticsearch 索引（包含向量数据）。
  * **人工复核**: 对于 AI 无法确定的或被驳回的申请，管理员可查看 AI 的推理过程 (`reasoning`) 和推荐列表 (`suggestion`)，进行快速人工裁决。

-----

## 数据库设计概览

  * **StandardAward (标准奖项表)**: 存储奖项元数据。
  * **AwardSubmission (提交记录表)**: 存储学生提交记录、状态、OCR 原始数据 (JSON)、AI 建议 (JSON) 及最终分数。
  * **Student / Admin**: 用户与权限表。

-----

## 快速开始 (Getting Started)

### 前置要求

  * JDK 17+
  * MySQL 8.0+
  * Redis
  * Elasticsearch 8.x/9.x
  * RocketMQ 4.x/5.x
  * 阿里云 OSS Bucket
  * 阿里云 DashScope API Key

### 配置

在 `application-dev.yml` 中配置以下环境变量：

```yaml
MYSQL_PASSWORD: 您的数据库密码
REDIS_PASSWORD: 您的Redis密码
ELASTICSEARCH_API_KEY: ES访问密钥
DASH_SCOPE_API_KEY: 阿里百炼大模型Key
ALIYUN_OSS_ACCESS_KEY_ID: OSS Key ID
ALIYUN_OSS_ACCESS_KEY_SECRET: OSS Key Secret
ALIYUN_OSS_BUCKET_NAME: Bucket名称
JWT_SECRET: JWT签名密钥
```

### 运行

```bash
mvn clean package
java -jar target/AwardProcessor-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

-----
