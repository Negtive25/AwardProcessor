package org.com.code.certificateProcessor.controller;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.Valid;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.com.code.certificateProcessor.pojo.StandardAward;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.CreateGroup;
import org.com.code.certificateProcessor.pojo.dto.groupInterface.UpdateGroup;
import org.com.code.certificateProcessor.pojo.dto.request.*;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.com.code.certificateProcessor.service.standardAward.StandardAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
    public ResponseHandler createBatch(@Validated(CreateGroup.class) @RequestBody List<StandardAwardRequest> standardAwardRequestList) {
        ULID ulid = new ULID();
        for (StandardAwardRequest standardAwardRequest : standardAwardRequestList) {
            standardAwardRequest.setStandardAwardId(ulid.nextULID());
        }
        standardAwardService.addBatchStandardAward(standardAwardRequestList);
        return new ResponseHandler(ResponseHandler.SUCCESS, "创建成功");
    }

    @RequestMapping("/updateBatch")
    public ResponseHandler updateBatch(@Validated(UpdateGroup.class) @RequestBody List<StandardAwardRequest> standardAwardRequestList) {
        List<StandardAwardRequest> toUpdateList = new ArrayList<>();
        for (StandardAwardRequest standardAwardRequest : standardAwardRequestList) {
           boolean flag = false;
           if(standardAwardRequest.getCategory()!=null||
                   standardAwardRequest.getLevel()!=null||
                   standardAwardRequest.getScore()!=null)
               flag = true;
           if(flag)
               toUpdateList.add(standardAwardRequest);
        }
        if (toUpdateList.size() > 0)
            standardAwardService.updateBatchStandardAward(toUpdateList);
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
