package org.com.code.certificateProcessor.service;

import org.com.code.certificateProcessor.pojo.dto.request.CursorPageRequest;
import org.com.code.certificateProcessor.pojo.dto.response.CursorPageResponse;

import java.util.List;
import java.util.function.Function;

public abstract class BaseCursorPageService<T> {

    /**
     * 通用分页执行器
     * @param lastStrId 游标
     * @param pageSize 每页条数
     * @param queryExecutor 具体的查询实现（方法引用）
     */
    public CursorPageResponse<T> fetchPage(
            String lastStrId,
            int pageSize,
            CursorQueryExecutor<T> queryExecutor,
            Function<T, String> cursorExtractor, // 提取游标字段
            String condition
    ) {
        List<T> list = queryExecutor.query(lastStrId, pageSize,condition);
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
    public interface CursorQueryExecutor<T> {
        List<T> query(String lastStrId, Integer pageSize,String status);
    }

}
