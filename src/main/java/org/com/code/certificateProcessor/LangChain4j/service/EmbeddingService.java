package org.com.code.certificateProcessor.LangChain4j.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.com.code.certificateProcessor.LangChain4j.config.AgentConfig;
import org.com.code.certificateProcessor.exeption.AIModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingService(AgentConfig agentConfig) {
        this.embeddingModel = agentConfig.builder()
                .embeddingModel(AgentConfig.textEmbeddingModel);
    }

    /**
     * 单文本向量化方法
     * @param text
     * @return
     */
    public float[] getEmbedding(String text) {
        Response<Embedding> response = embeddingModel.embed(text);
        if (response != null && response.content() != null) {
            return response.content().vector();
        }
        throw new AIModelException("获取Embedding失败");
    }

    /**
     * 批量文本向量化方法
     * @param texts 需要向量化的文本文档列表
     * @return 向量列表，顺序与输入文本一致
     */
    public List<float[]> getEmbeddings(List<String> texts) {
        // 过滤掉null和空字符串
        List<String> validTexts = texts.stream()
                .filter(Objects::nonNull)  // 过滤null值
                .filter(text -> !text.trim().isEmpty())  // 过滤空字符串
                .collect(Collectors.toList());

        // 如果没有有效文本，返回空列表或抛出适当异常
        if (validTexts.isEmpty()) {
            return new ArrayList<>();
        }

        // embedAll方法专为批量处理设计，它会一次性将所有文本发送到模型API
        // 将字符串列表转换为TextSegment列表
        List<TextSegment> textSegments = texts.stream()
                .map(TextSegment::from)
                .collect(Collectors.toList());

        Response<List<Embedding>> response = embeddingModel.embedAll(textSegments);
        if (response != null && response.content() != null) {
            return response.content().stream()
                    .map(Embedding::vector)
                    .collect(Collectors.toList());
        }
        throw new AIModelException("批量获取Embedding失败");
    }
}