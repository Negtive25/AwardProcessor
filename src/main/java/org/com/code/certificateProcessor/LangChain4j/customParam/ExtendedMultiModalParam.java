package org.com.code.certificateProcessor.LangChain4j.customParam;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import lombok.Getter;

import java.util.Map;

@Getter
public class ExtendedMultiModalParam extends MultiModalConversationParam {
    private String responseFormat;

    protected ExtendedMultiModalParam(ExtendedBuilder b) {
        super(b);
        this.responseFormat = b.responseFormat;
    }


    public static ExtendedBuilder builder() {
        return new ExtendedBuilder();
    }

    public static class ExtendedBuilder extends MultiModalConversationParamBuilder<ExtendedMultiModalParam, ExtendedBuilder> {
        private String responseFormat;

        public ExtendedBuilder responseFormat(String format) {
            this.responseFormat = format;
            return this;
        }

        @Override
        protected ExtendedBuilder self() {
            return this;
        }

        @Override
        public ExtendedMultiModalParam build() {
            return new ExtendedMultiModalParam(this);
        }
    }

    // 重写 getHttpBody 或 getParameters 来注入 response_format
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = super.getParameters();
        if (this.responseFormat != null) {
            params.put("response_format", this.responseFormat);
        }
        return params;
    }
}
