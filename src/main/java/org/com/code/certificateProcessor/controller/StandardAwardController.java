package org.com.code.certificateProcessor.controller;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.Valid;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.request.*;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.com.code.certificateProcessor.service.standardAward.StandardAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standardAward")
public class StandardAwardController {
    @Autowired
    private StandardAwardService standardAwardService;

    @GetMapping("/get")
    public ResponseHandler get(@Valid @RequestBody CursorPageRequest cursorPageRequest) {
        CursorPageResponse<StandardAward> standardAwardResponse = standardAwardService.cursorQueryStandardAward(cursorPageRequest.getLastId(), cursorPageRequest.getPageSize());
        return new ResponseHandler(ResponseHandler.SUCCESS, "获取成功",standardAwardResponse );
    }

    @RequestMapping("/createBatch")
    public ResponseHandler createBatch(@Valid @RequestBody CreateBatchStandardAwardRequest createBatchStandardAwardRequest) {
        ULID ulid = new ULID();
        List<Map<String, Object>> standardAwardList = new ArrayList<>();
        for (CreateStandardAward standardAward : createBatchStandardAwardRequest.getStandardAwardRequestList()) {
            Map<String, Object> standardAwardMap = new HashMap<>();
            standardAwardMap.put("standardAwardId", ulid.nextULID());
            standardAwardMap.put("name", standardAward.getName());
            standardAwardMap.put("category", standardAward.getCategory());
            standardAwardMap.put("level", standardAward.getLevel());
            standardAwardMap.put("score", standardAward.getScore());
            standardAwardList.add(standardAwardMap);
        }
        standardAwardService.addBatchStandardAward(standardAwardList);
        return new ResponseHandler(ResponseHandler.SUCCESS, "创建成功");
    }

    @RequestMapping("/updateBatch")
    public ResponseHandler updateBatch(@Valid @RequestBody UpdateBatchStandardAwardRequest updateBatchStandardAwardRequest) {
        List<Map<String, Object>> standardAwardList = new ArrayList<>();
        for (UpdateStandardAward standardAward : updateBatchStandardAwardRequest.getUpdateStandardAwardRequestList()) {
            Map<String, Object> standardAwardMap = new HashMap<>();
            standardAwardMap.put("standardAwardId", standardAward.getStandardAwardId());
            if (standardAward.getName() != null) {
                standardAwardMap.put("name", standardAward.getName());
            }
            if (standardAward.getCategory() != null) {
                standardAwardMap.put("category", standardAward.getCategory());
            }
            if (standardAward.getLevel() != null) {
                standardAwardMap.put("level", standardAward.getLevel());
            }
            if (standardAward.getScore() != null) {
                standardAwardMap.put("score", standardAward.getScore());
            }
            if (standardAwardMap.keySet().size() > 1)
                standardAwardList.add(standardAwardMap);
        }
        if (standardAwardList.size() > 0)
            standardAwardService.updateBatchStandardAward(standardAwardList);
        return new ResponseHandler(ResponseHandler.SUCCESS, "更新成功");
    }

    @RequestMapping("/delete")
    public ResponseHandler delete(
            @NotBlank(message = "standardAwardId不能为空")
            String standardAwardId) {
        standardAwardService.deleteStandardAward(standardAwardId);
        return new ResponseHandler(ResponseHandler.SUCCESS, "删除成功");
    }
}
