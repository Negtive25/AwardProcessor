package org.com.code.certificateProcessor.LangChain4j.service;

import org.com.code.certificateProcessor.LangChain4j.agent.ClassificationAgent;
import org.com.code.certificateProcessor.LangChain4j.config.AgentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassificationService {
    private final ClassificationAgent classificationAgent;

    @Autowired
    public ClassificationService(AgentConfig agentConfig) {
       this.classificationAgent = agentConfig.builder()
               .chatModel(AgentConfig.qwenFlash)
               .buildClassificationAgent();
    }

    public ClassificationAgent getClassificationAgent() {
        return classificationAgent;
    }
}
