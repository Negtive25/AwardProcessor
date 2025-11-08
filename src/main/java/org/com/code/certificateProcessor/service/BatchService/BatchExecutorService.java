package org.com.code.certificateProcessor.service.BatchService;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;

@Service
public class BatchExecutorService {
    public <T,U> void executeBatch(
            SqlSessionFactory sqlSessionFactory,
            Class<T> mapperClass, List<U> list,
            BiConsumer<T, U> operation,// 告诉方法如何执行单个实体
            int batchSize) {

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            T mapper = sqlSession.getMapper(mapperClass);
            int count = 0;

            for (U item : list) {
                // 调用方法，SQL 被添加到 "托盘" 上
                operation.accept(mapper, item);
                count++;

                if (count % batchSize == 0) {
                    sqlSession.flushStatements(); // "清空托盘"，发送 SQL
                    sqlSession.clearCache();      // 清理内存，防止 OOM
                }
            }

            sqlSession.flushStatements(); //处理最后一批
            sqlSession.clearCache();
        }
    }
}
