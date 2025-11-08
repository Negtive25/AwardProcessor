package org.com.code.certificateProcessor.ElasticSearch.Service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.com.code.certificateProcessor.ElasticSearch.ESConst;
import org.com.code.certificateProcessor.ElasticSearch.Service.ElasticUtil;
import org.com.code.certificateProcessor.ElasticSearch.Service.ESStandardAwardService;
import org.com.code.certificateProcessor.LangChain4j.service.EmbeddingService;
import org.com.code.certificateProcessor.exeption.ElasticSearchException;
import org.com.code.certificateProcessor.pojo.dto.document.StandardAwardDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void bulkCreateStandardAwardIndex(List<StandardAwardDocument> standardAwardDocumentList) {
        try{
            List<String> standardAwardNameList = standardAwardDocumentList.stream().map(StandardAwardDocument::getName).toList();
            List<float[]> ebeddingList = embeddingService.getEmbeddings(standardAwardNameList);
            elasticUtil.bulkIndex(standardAwardDocumentList, ESConst.STANDARD_AWARD, ebeddingList);
        }catch (Exception e){
            throw new ElasticSearchException("创建标准奖状ES索引失败",e);
        }
    }

    @Override
    public void deleteStandardAwardIndex(List<String> standardAwardIdList) {
        try{
            elasticUtil.bulkDelete(standardAwardIdList, ESConst.STANDARD_AWARD);
        }catch (Exception e){
            throw new ElasticSearchException("删除标准奖状ES索引失败",e);
        }
    }

    @Override
    public void updateStandardAwardIndex(List<StandardAwardDocument> standardAwardDocumentList) {
        try{
            List<String> standardAwardNameList = standardAwardDocumentList.stream().map(StandardAwardDocument::getName).toList();
            List<float[]> ebeddingList = embeddingService.getEmbeddings(standardAwardNameList);
            elasticUtil.bulkUpdate(standardAwardDocumentList, ESConst.STANDARD_AWARD,ebeddingList);
        }catch (Exception e){
            throw new ElasticSearchException("更新标准奖状ES索引失败",e);
        }
    }
}
