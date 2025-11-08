package org.com.code.certificateProcessor.LangChain4j.service;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import org.com.code.certificateProcessor.LangChain4j.config.AgentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class OCRService {
    @Autowired
    AgentConfig agentConfig;

    private static final String prompt = "请首先判断图片是否为一张奖状或证书。\n" +
            "\n" +
            "- **如果是奖状或证书**：\n" +
            "  请识别图片中的信息，并严格按照如下 JSON 格式输出。\n" +
            "  注意：\n" +
            "  - 如果图片内容是奖状或证书, 添加字段 \"ifCertification\":\"Yes\"。\n" +
            "  - 如果图片中的字段可能是 和人的姓名相关的，请将它们统一提取到 \"studentName\" 字段中。\n" +
            "  - 如果图片中的字段可能是 和奖状或者证书名称相关的，请将它们统一提取到 \"awardName\" 字段中。\n" +
            "  - 如果图片中的字段可能是 和奖状或者证书颁布时间相关的,请将它们统一提取到 \"awardDate\" 字段中。 \n" +
            "  - 日期必须被严格格式化为 YYYY-MM-DD 标准格式。\n" +
            "  - 如果日期缺少“日”（例如 \"2025年7月\"），则默认为该月的1号，输出 \"2025-07-01\"。\n" +
            "  - 如果日期缺少“月”和“日”（例如 \"2025年\"），则默认为该年的1月1日，输出 \"2025-01-01\"。\n" +
            "  - 如果图片中没有任何与日期相关的字段,则 \"awardDate\" 字段的值应为 null。\n" +
            "  - 格式示例：\n" +
            "    {\n" +
            "      \"studentName\": \"学生姓名\",\n" +
            "      \"awardName\": \"奖项名称\"\n" +
            "      \"awardDate\": \"2025-12-09\"\n" +
            "      \"ifCertification\":\"Yes\"\n" +
            "    }\n" +
            "\n" +
            "- **如果图片内容不是奖状或证书**（例如，是风景、动物、日常照片等）：\n" +
            "  请严格按照如下 JSON 格式输出：\n" +
            "    {\n" +
            "      \"ifCertification\": \"No\",\n" +
            "      \"reason\": \"概括是怎么样的一张图\"\n" +
            "    }";

    public String getOCRResult(String imageURL) throws NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", imageURL),
                        Collections.singletonMap("text", prompt))
                ).build();

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(agentConfig.getApiKey())
                .model(AgentConfig.qwen3VLFlash)
                .message(userMessage)
                .build();
        return conv.call(param).getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
    }
}