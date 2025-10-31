package org.com.code.certificateProcessor.LangChain4j.agent;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.UserMessage;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.AwardInfo;
import org.springframework.stereotype.Component;


@Component
public interface OCRAgent {
    @UserMessage(
            """
请首先判断图片是否为一张奖状或证书。

- **如果是奖状或证书**：
  请识别图片中的信息，并严格按照如下 JSON 格式输出。
  注意：
  - 如果图片内容是奖状或证书, 添加字段 "ifCertification":"Yes"。
  - 如果图片中的字段可能是 和人的姓名相关的，请将它们统一提取到 "studentName" 字段中。
  - 如果图片中的字段可能是 和奖状或者证书名称相关的，请将它们统一提取到 "awardName" 字段中。
  - 如果图片中的字段可能是 和奖状或者证书颁布时间相关的,请将它们统一提取到 "awardDate" 字段中。 
  - 日期必须被严格格式化为 YYYY-MM-DD 标准格式。
  - 如果日期缺少“日”（例如 "2025年7月"），则默认为该月的1号，输出 "2025-07-01"。
  - 如果日期缺少“月”和“日”（例如 "2025年"），则默认为该年的1月1日，输出 "2025-01-01"。
  - 如果图片中没有任何与日期相关的字段,则 "awardDate" 字段的值应为 null。
  - 格式示例：
    {
      "studentName": "学生姓名",
      "awardName": "标准奖项名称"
      "awardDate": "2025年12月9日"
      "ifCertification":"Yes"
    }

- **如果图片内容不是奖状或证书**（例如，是风景、动物、日常照片等）：
  请严格按照如下 JSON 格式输出：
    {
      "ifCertification": "No"
    }

最终输出必须是合法的 JSON，不要包含任何其他内容、解释、Markdown 或 ```json 包裹。
"""
    )
    AwardInfo extractWordFromPicture(Image image);
}
