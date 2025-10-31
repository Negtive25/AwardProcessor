package org.com.code.certificateProcessor.LangChain4j.service;

import org.com.code.certificateProcessor.LangChain4j.agent.OCRAgent;
import org.com.code.certificateProcessor.LangChain4j.config.AgentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OCRService {
    private final OCRAgent ocrAgent;

    @Autowired
    public OCRService(AgentConfig agentConfig) {
        this.ocrAgent = agentConfig.builder()
                .chatModel(AgentConfig.qwen3VLFlash)
                .buildOCRAgent();
    }

    public OCRAgent getOcrAgent() {
        return ocrAgent;
    }
}
