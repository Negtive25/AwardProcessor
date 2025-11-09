package org.com.code.certificateProcessor.service;

import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;
import org.com.code.certificateProcessor.pojo.entity.StandardAward;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public abstract class BaseCursorPageService<T> {

    /**
     * 通用分页执行器
     * @param lastStrId 游标
     * @param pageSize 每页条数
     * @param queryExecutor 具体的查询实现（方法引用）
     */
    public CursorPageResponse<T> fetchAwardSubmissionPage(
            String lastStrId,
            int pageSize,
            StudentCursorQueryAwardSubmissionExecutor<T> queryExecutor,
            Function<T, String> cursorExtractor, // 提取游标字段
            List<String> status,
            Boolean isAdmin,
            String studentId
    ) {
        List<T> list = queryExecutor.query(lastStrId, pageSize,status,isAdmin,studentId);
        return getTCursorPageResponse(pageSize, cursorExtractor, list);
    }

    public CursorPageResponse<T> fetchStandardAwardPage(
            CursorPageRequest cursorPageRequest,
            StandardAward standardAward,
            CursorQueryStandardAwardExecutor queryExecutor,
            Function<T, String> cursorExtractor
            ){
        List<T> list = queryExecutor.query(cursorPageRequest, standardAward);
        return getTCursorPageResponse(cursorPageRequest.getPageSize(), cursorExtractor, list);
    }

    public CursorPageResponse<T> fetchPage(
            String lastStrId,
            int pageSize,
            CursorQueryExecutor<T> queryExecutor,
            Function<T, String> cursorExtractor
    ) {
        List<T> list = queryExecutor.query(lastStrId, pageSize);
        return getTCursorPageResponse(pageSize, cursorExtractor, list);
    }

    private static <T> @NotNull CursorPageResponse<T> getTCursorPageResponse(int pageSize, Function<T, String> cursorExtractor, List<T> list) {
        CursorPageResponse<T> resp = new CursorPageResponse<>();
        resp.setList(list);

        if (list == null || list.isEmpty()) {
            resp.setHasNext(false);
            return resp;
        }

        String minId = cursorExtractor.apply(list.get(0));
        String maxId = cursorExtractor.apply(list.get(list.size() - 1));
        resp.setMinId(minId);
        resp.setMaxId(maxId);

        resp.setHasNext(list.size() >= pageSize);
        return resp;
    }

    @FunctionalInterface
    public interface StudentCursorQueryAwardSubmissionExecutor<T> {
        List<T> query(String lastStrId, Integer pageSize,
                     List<String> status, Boolean isAdmin, String studentId);
    }

    @FunctionalInterface
    public interface CursorQueryExecutor<T> {
        List<T> query(String lastStrId, Integer pageSize);
    }
    @FunctionalInterface
    public interface CursorQueryStandardAwardExecutor<T> {
        List<T> query(CursorPageRequest cursorPageRequest, StandardAward standardAward);
    }
}
