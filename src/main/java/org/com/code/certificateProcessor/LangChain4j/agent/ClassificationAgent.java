package org.com.code.certificateProcessor.LangChain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.com.code.certificateProcessor.pojo.modelInfo.AwardClassification;
import org.com.code.certificateProcessor.pojo.modelInfo.AwardInfo;
import org.com.code.certificateProcessor.pojo.modelInfo.DeduplicationResult;
import org.com.code.certificateProcessor.pojo.entity.AwardSubmission;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClassificationAgent {

    @SystemMessage(
            "你是一个严谨且格式严格的教务系统审核助手。" +
                    "你的任务是：根据学生上传奖状的 OCR 识别信息，从一个“标准奖项候选列表”中匹配出最准确的标准奖项。" +
                    "无论是否找到匹配项，你都必须严格输出一个完整的 JSON 对象，其结构必须符合 AwardClassification 类的定义。" +
                    "如果 AwardClassification 的某些字段没有对应值，请将它们的值设为 null，而不是省略。"
    )
    @UserMessage(
            "请帮我分析以下数据：\n" +
                    "1. **学生奖状识别信息 (来自 OCR)**:\n" +
                    "```json\n{{ocrInfo}}\n```\n" +
                    "2. **标准奖项候选列表 (来自数据库搜索)**:\n" +
                    "```json\n{{candidates}}\n```\n" +
                    "**任务要求**:\n" +
                    "1. 对比学生奖状的 `awardName`（来自 OCR 信息）与候选列表中每个奖项的 `officialName`。\n" +
                    "2. 你需要输出一个完整的 `AwardClassification` JSON 对象，结构如下：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"matchFound\": true 或 false,\n" +
                    "  \"matchedAwardId\": \"string 或 null\",\n" +
                    "  \"matchedAwardName\": \"string 或 null\",\n" +
                    "  \"reasoning\": \"string (说明匹配理由或无法匹配的原因)\"\n" +
                    "}\n" +
                    "```\n" +
                    "3. 输出时只返回这个 JSON 对象，**不要添加任何额外文本、解释或注释**。\n" +
                    "4. 若无法判断或没有合适匹配，请设置：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"matchFound\": false,\n" +
                    "  \"matchedAwardId\": null,\n" +
                    "  \"matchedAwardName\": null,\n" +
                    "  \"reasoning\": \"无法根据 OCR 信息与候选列表确定匹配。\"\n" +
                    "}\n" +
                    "```"
    )
    AwardClassification classifyAward(
            @V("ocrInfo") AwardInfo ocrInfo,
            @V("candidates") List<StandardAward> candidates
    );


    @SystemMessage({
            "你是一个严格的教务系统查重助手。",
            "你的任务是对比一个“新提交的奖项”和一组“数据库中的旧奖项记录”，判断新奖项是否为其中某一个旧奖项的重复提交。",
            "核心规则：",
            "1. 如果奖项名称（awardName）相似，但获奖日期（awardDate）明显不同（如“2024年” vs “2025年”），则不是重复提交。",
            "2. 只有当奖项名称和获奖日期都高度一致时，才认为是重复提交。",
            "3. 如果多个旧奖项都与新奖项高度相似，请选择最相似的那一个作为匹配结果。",
            "4. 你的输出必须是严格的 JSON 格式，结构必须符合 DeduplicationResult 类的定义。",
            "5. 如果 DeduplicationResult 的某些字段没有对应值，请将其设置为 null，而不是省略。"
    })
    @UserMessage(
            "请分析以下数据：\n" +
                    "\n" +
                    "1. **新提交的奖项信息 (来自 OCR 提取)**:\n" +
                    "```json\n{{newAward}}\n```\n" +
                    "\n" +
                    "2. **数据库中的旧奖项记录列表**:\n" +
                    "```json\n{{oldAward}}\n```\n" +
                    "\n" +
                    "**任务要求**:\n" +
                    "1. 遍历旧奖项列表，与新奖项逐一对比其 `awardName` 和 `awardDate`。\n" +
                    "2. 如果找到一个高度匹配的旧奖项，请设置：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"duplicated\": true,\n" +
                    "  \"matchedAwardId\": \"旧奖项的ID\",\n" +
                    "  \"matchedAwardName\": \"旧奖项的名称\",\n" +
                    "  \"reasoning\": \"说明为什么认为两者是同一个奖项\"\n" +
                    "}\n" +
                    "```\n" +
                    "3. 如果没有任何旧奖项符合条件，请设置：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"duplicated\": false,\n" +
                    "  \"matchedAwardId\": null,\n" +
                    "  \"matchedAwardName\": null,\n" +
                    "  \"reasoning\": \"说明为什么判定不是重复提交\"\n" +
                    "}\n" +
                    "```\n" +
                    "4. 输出时只返回这个 JSON 对象，**不要添加任何多余文字、解释或注释**。"
    )
    DeduplicationResult checkForDuplicate(
            @V("newAward") AwardInfo newAward,
            @V("oldAward") List<AwardSubmission> oldAward
    );

}
