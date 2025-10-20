package org.com.code.certificateProcessor.service.standardAward.impl;

import org.com.code.certificateProcessor.ElasticSearch.Service.ESStandardAwardService;
import org.com.code.certificateProcessor.exeption.StandardAwardException;
import org.com.code.certificateProcessor.mapper.StandardAwardMapper;
import org.com.code.certificateProcessor.service.standardAward.StandardAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class StandardAwardImpl implements StandardAwardService {
    @Autowired
    StandardAwardMapper standardAwardMapper;
    @Autowired
    ESStandardAwardService esStandardAwardService;

    @Override
    @Transactional
    public void addBatchStandardAward(List<Map<String, Object>> standardAwardList) {
        try{
            standardAwardMapper.addBatchStandardAward(standardAwardList);
            esStandardAwardService.bulkCreateStandardAwardIndex(standardAwardList);
        } catch (Exception e) {
            throw new StandardAwardException("批量创建标准奖状列表失败",e);
        }
    }

    @Override
    @Transactional
    public void updateBatchStandardAward(List<Map<String, Object>> standardAwardList) {
        try{
            standardAwardMapper.updateBatchStandardAward(standardAwardList);
            esStandardAwardService.updateStandardAwardIndex(standardAwardList);
        } catch (Exception e) {
            throw new StandardAwardException("批量更新标准奖状列表失败",e);
        }
    }

    @Override
    @Transactional
    public void deleteStandardAward(String standardAwardId) {
        try{
            standardAwardMapper.deleteStandardAward(standardAwardId);
            esStandardAwardService.deleteStandardAwardIndex(standardAwardId);
        } catch (Exception e) {
            throw new StandardAwardException("删除标准奖状列表失败",e);
        }
    }
}
