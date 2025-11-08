package org.com.code.certificateProcessor.service.BatchService;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class BatchQueryService {

    /**
     * 分批执行批量查询。
     *
     * @param sqlSessionFactory
     * @param mapperClass     Mapper 接口的 Class
     * @param idList          要查询的总 ID 列表
     * @param batchSize       每次 IN ( ... ) 查询的 ID 数量（建议 500 或 1000）
     * @param queryOperation  一个函数，告诉本方法如何调用 Mapper 的批量查询
     * (Mapper 实例, ID子列表) -> (查询结果列表)
     * @param <M>             Mapper 接口类型 (e.g., DocumentMapper.class)
     * @param <I>             ID 的类型 (e.g., Integer, Long, String)
     * @param <R>             返回的实体类型 (e.g., Document)
     * @return 汇总后的总结果列表
     */
    public <M, I, R> List<R> executeBatchQuery(
            SqlSessionFactory sqlSessionFactory,
            Class<M> mapperClass,
            List<I> idList,
            int batchSize,
            BiFunction<M, List<I>, List<R>> queryOperation) {

        List<R> allResults = new ArrayList<>();

        if (idList == null || idList.isEmpty()) {
            return allResults;
        }

        // 使用默认的 ExecutorType.SIMPLE，不需要 BATCH
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            M mapper = sqlSession.getMapper(mapperClass);

            int totalSize = idList.size();

            // 在 Java 层面进行“批处理”
            for (int fromIndex = 0; fromIndex < totalSize; fromIndex += batchSize) {
                int toIndex = Math.min(fromIndex + batchSize, totalSize);

                // 截取子列表
                List<I> subList = idList.subList(fromIndex, toIndex);

                // 调用真正的 Mapper 方法，例如：
                // (mapper, subList) -> mapper.selectDocumentsByIds(subList)
                List<R> batchResults = queryOperation.apply(mapper, subList);

                if (batchResults != null && !batchResults.isEmpty()) {
                    allResults.addAll(batchResults);
                }

                sqlSession.flushStatements(); //处理最后一批
                sqlSession.clearCache();
            }
        }

        return allResults;
    }
}