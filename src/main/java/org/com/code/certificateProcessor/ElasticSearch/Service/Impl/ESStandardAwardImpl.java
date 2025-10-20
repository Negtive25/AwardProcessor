package org.com.code.certificateProcessor.ElasticSearch.Service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import org.com.code.certificateProcessor.ElasticSearch.Service.ElasticUtil;
import org.com.code.certificateProcessor.ElasticSearch.Service.ESStandardAwardService;
import org.com.code.certificateProcessor.LangChain4j.service.EmbeddingService;
import org.com.code.certificateProcessor.exeption.ElasticSearchException;
import org.com.code.certificateProcessor.pojo.enums.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ESStandardAwardImpl implements ESStandardAwardService {
    @Autowired
    @Qualifier("node1")
    private ElasticsearchClient client;
    @Autowired
    private ElasticUtil elasticUtil;
    @Autowired
    private EmbeddingService embeddingService;
    @Override
    public void bulkCreateStandardAwardIndex(List<Map<String, Object>> standardAwardList) throws IOException {
        try{
            List<String> standardAwardNameList = standardAwardList.stream().map(standardAward -> standardAward.get("name").toString()).toList();
            List<float[]> ebeddingList = embeddingService.getEmbeddings(standardAwardNameList);

            List<Map<String,Object>> documents = standardAwardList.stream()
                    .map(standardAward -> {
                        Map<String, Object> document = new HashMap<>();
                        document.put("standardAwardId", standardAward.get("standardAwardId"));
                        document.put("name", standardAward.get("name"));
                        document.put("isActive", true);
                        return document;
                    })
                    .toList();
            elasticUtil.bulkIndex(documents, ContentType.STANDARD_AWARD.getType(), ebeddingList);
        }catch (Exception e){
            throw new ElasticSearchException("创建标准奖状ES索引失败",e);
        }
    }

    @Override
    public void deleteStandardAwardIndex(String standardAwardId) {
        try{
            client.delete(d -> d.index(ContentType.STANDARD_AWARD.getType()).id(standardAwardId));
        }catch (Exception e){
            throw new ElasticSearchException("删除标准奖状ES索引失败",e);
        }
    }

    @Override
    public void updateStandardAwardIndex(List<Map<String, Object>> standardAwardList) {
        try{
            List<String> standardAwardNameList = standardAwardList.stream().map(standardAward -> standardAward.get("name").toString()).toList();
            List<float[]> ebeddingList = embeddingService.getEmbeddings(standardAwardNameList);
            elasticUtil.bulkUpdate(standardAwardList,ContentType.STANDARD_AWARD.getType(),ebeddingList);
        }catch (Exception e){
            throw new ElasticSearchException("更新标准奖状ES索引失败",e);
        }
    }
}
