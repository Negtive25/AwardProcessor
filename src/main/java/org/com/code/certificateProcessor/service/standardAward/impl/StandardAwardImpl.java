package org.com.code.certificateProcessor.service.standardAward.impl;

import de.huxhorn.sulky.ulid.ULID;
import org.apache.ibatis.session.SqlSessionFactory;
import org.com.code.certificateProcessor.ElasticSearch.Service.ESStandardAwardService;
import org.com.code.certificateProcessor.exeption.ResourceNotFoundException;
import org.com.code.certificateProcessor.exeption.StandardAwardException;
import org.com.code.certificateProcessor.mapper.StandardAwardMapper;
import org.com.code.certificateProcessor.pojo.dto.document.StandardAwardDocument;
import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.AdminStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.dto.response.standardAwardResponse.BaseStandardAwardInfoResponse;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.request.standardAwardRequest.StandardAwardRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.structMap.StandardAwardStructMap;
import org.com.code.certificateProcessor.service.BaseCursorPageService;
import org.com.code.certificateProcessor.service.BatchService.BatchExecutorService;
import org.com.code.certificateProcessor.service.standardAward.StandardAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StandardAwardImpl extends BaseCursorPageService<StandardAward> implements StandardAwardService {
    @Autowired
    StandardAwardMapper standardAwardMapper;
    @Autowired
    ESStandardAwardService esStandardAwardService;
    @Autowired
    StandardAwardStructMap standardAwardStructMap;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    BatchExecutorService batchExecutorService;


    @Override
    public AdminStandardAwardInfoResponse getStandardAwardById(String standardAwardId) {
       try{
           StandardAward standardAward = standardAwardMapper.getStandardAwardById(standardAwardId);
           if(standardAward == null)
               throw new ResourceNotFoundException("标准奖状不存在");
           return standardAwardStructMap.toAdminStandardAwardInfoResponse(standardAward);
       }catch (StandardAwardException e){
           throw e;
       }catch (ResourceNotFoundException e){
           throw new StandardAwardException("查询标准奖状详情失败",e);
       }catch (Exception e){
           throw new StandardAwardException("查询标准奖状详情失败",e);
       }
    }

    @Override
    public CursorPageResponse<? extends BaseStandardAwardInfoResponse> cursorQueryStandardAward(CursorPageRequest cursorPageRequest,String studentId) {
         String lastId = cursorPageRequest.getLastId();
         int pageSize = cursorPageRequest.getPageSize();

         try{
           CursorPageResponse<StandardAward> cursorPage;
           if(pageSize < 0)
               cursorPage = fetchPage(lastId, - pageSize, standardAwardMapper::getPreviousStandardAward, StandardAward::getStandardAwardId);
           else
               cursorPage = fetchPage(lastId, pageSize, standardAwardMapper::getLatterStandardAward, StandardAward::getStandardAwardId);

           List<? extends BaseStandardAwardInfoResponse> standardAwardInfoResponses;
           if(studentId == null)
               standardAwardInfoResponses = standardAwardStructMap.toAdminStandardAwardInfoResponseList(cursorPage.getList());
           else
               standardAwardInfoResponses = standardAwardStructMap.toBaseStandardAwardInfoResponseList(cursorPage.getList());

           return new CursorPageResponse<>(standardAwardInfoResponses,cursorPage.getMinId(),cursorPage.getMaxId(),cursorPage.getHasNext());
       }catch (Exception e){
           throw new StandardAwardException("查询标准奖状列表失败",e);
       }
    }

    @Override
    @Transactional
    public void addBatchStandardAward(List<StandardAwardRequest> standardAwardRequestList) {
        try{
            List<StandardAward> standardAwardList = standardAwardStructMap.toStandardAwardList(standardAwardRequestList);

            String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
            ULID ulid = new ULID();
            for (StandardAward standardAward : standardAwardList) {
                standardAward.setStandardAwardId(ulid.nextULID());
                standardAward.setCreatedBy(createdBy);
                standardAward.setUpdatedBy(createdBy);
            }

            final int batchSize = 1000;

            batchExecutorService.executeBatch(
                    sqlSessionFactory,
                    StandardAwardMapper.class, standardAwardList,
                    StandardAwardMapper::addStandardAward,
                    batchSize
            );
                
            List<StandardAwardDocument> standardAwardDocumentList = standardAwardStructMap.toStandardAwardDocumentList(standardAwardList);
            esStandardAwardService.bulkCreateStandardAwardIndex(standardAwardDocumentList);
        } catch (Exception e) {
            throw new StandardAwardException("批量创建标准奖状列表失败",e);
        }
    }

    @Override
    @Transactional
    public void updateBatchStandardAward(List<StandardAwardRequest> standardAwardRequestList) {
        try{
            List<StandardAward> standardAwardList = standardAwardStructMap.toStandardAwardList(standardAwardRequestList);
            String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
            for (StandardAward standardAward : standardAwardList) {
                standardAward.setUpdatedBy(updatedBy);
            }

            final int batchSize = 1000;

            batchExecutorService.executeBatch(
                    sqlSessionFactory,
                    StandardAwardMapper.class, standardAwardList,
                    StandardAwardMapper::updateStandardAward,
                    batchSize
            );

            List<StandardAwardDocument> standardAwardDocumentList = standardAwardStructMap.toStandardAwardDocumentList(standardAwardList);
            esStandardAwardService.updateStandardAwardIndex(standardAwardDocumentList);
        } catch (Exception e) {
            throw new StandardAwardException("批量更新标准奖状列表失败",e);
        }
    }

    @Override
    @Transactional
    public void deleteStandardAward(List<String> standardAwardIdList) {
        try{
            final int batchSize = 1000;

            batchExecutorService.executeBatch(
                    sqlSessionFactory,
                    StandardAwardMapper.class,standardAwardIdList,
                    StandardAwardMapper::deleteStandardAward,
                    batchSize
            );

            esStandardAwardService.deleteStandardAwardIndex(standardAwardIdList);
        } catch (Exception e) {
            throw new StandardAwardException("删除标准奖状列表失败",e);
        }
    }
}
