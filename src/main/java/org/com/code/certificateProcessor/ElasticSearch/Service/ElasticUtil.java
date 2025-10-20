package org.com.code.certificateProcessor.ElasticSearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;

import org.com.code.certificateProcessor.LangChain4j.config.ModelConfig;
import org.com.code.certificateProcessor.LangChain4j.service.EmbeddingService;
import org.com.code.certificateProcessor.LangChain4j.modelInfo.VectorDoc;
import org.com.code.certificateProcessor.pojo.enums.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ElasticUtil {
    @Autowired
    @Qualifier("node1")
    private ElasticsearchClient client;
    @Autowired
    private EmbeddingService embeddingService;

    /**
     * 搜索相似度阈值,只有高于这个阈值，才会被knn的向量相似度查询命中返回给用户,
     * AI的RAG搜索的阈值我设置成0.8，推荐搜索的阈值我设置成0.7
     */
    public static final float RAGSearchMinSimilarity = 0.8f;
    public static final float UserRecommendSearchMiniSimilarity = 0.5f;
    public static final int resultLimit  = 30;

    public static final double textWeight = 0.7;
    public static final double knnWeight = 1.0;

    public static final int RRF_CONSTANT=60;

    /**
     * 批量索引方法
     * @param documents
     * @param indexName
     * @param vectorList
     * @throws IOException
     */
    public void bulkIndex(List<Map<String, Object>> documents, String indexName,List<float[]> vectorList) throws IOException {
        if (documents == null || documents.isEmpty()) {
            return; // 如果没有文档，则直接返回
        }

        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).put("name_vector", vectorList.get(i));
        }

        // 1. 创建一个BulkOperation列表
        List<BulkOperation> bulkOperations = new ArrayList<>();

        for (Map<String, Object> doc : documents) {
            // 为每个文档创建一个 "index" 操作
            BulkOperation operation = BulkOperation.of(op -> op
                    .index(idx -> idx
                            .index(indexName)
                            .id((String)doc.get("standardAwardId")) // 这里需要一个唯一的ID
                            .document(doc)
                    )
            );
            bulkOperations.add(operation);
        }

        // 2. 构建并执行 BulkRequest
        BulkRequest bulkRequest = BulkRequest.of(b -> b
                .operations(bulkOperations)
        );

        client.bulk(bulkRequest);
    }

    public void bulkUpdate(List<Map<String, Object>> documentsToUpdate,String indexName,List<float[]> vectorList) throws IOException {
        if (documentsToUpdate == null || documentsToUpdate.isEmpty()) {
            return; // 如果没有文档，则直接返回
        }

        int j = 0;
        for (int i = 0; i < documentsToUpdate.size(); i++) {
            if(documentsToUpdate.get(i).get("name").toString() != null){
                documentsToUpdate.get(i).put("name_vector", vectorList.get(j));
                j++;
            }
        }

        List<BulkOperation> bulkOperations = new ArrayList<>();

        for (Map<String, Object> doc : documentsToUpdate) {
            BulkOperation operation = BulkOperation.of(op -> op
                    .update(idx -> idx
                            .index(indexName)
                            .id((String)doc.get("standardAwardId")) // 这里需要一个唯一的ID
                            .action(a -> a
                                    .doc(doc)
                            )
                    )
            );
            bulkOperations.add(operation);
        }
        BulkRequest bulkRequest = BulkRequest.of(b -> b
                .operations(bulkOperations)
        );
        client.bulk(bulkRequest);
    }

    public List<Long> searchContentBySimilarAverageVector(float[] array, String indexName, int similarContentNumber,List<String> excludeContentIdList) throws IOException {
        // 将 float[] 转换为 List<Float>
        List<Float> queryVector = new ArrayList<>();
        for (float f : array) {
            queryVector.add(f);
        }


        /**
         * 如果将 knn() 向量查询和 query() 布尔查询（用于排除ID）作为两个独立的、平级的参数设置在了顶层的 SearchRequest.Builder 上。
         *
         * 错误的代码结构示意：
         *
         * Java
         *
         * // SearchRequest.Builder
         * builder
         *     .knn(...)      // 这是一个顶层查询
         *     .query(...)    // 这是另一个平级的顶层查询
         * 这导致无意中构建了一个混合搜索 (Hybrid Search) 请求，而不是一个带前置过滤的 k-NN 搜索 (Filtered k-NN Search) 请求。
         *
         * 三、错误模式的真实含义 (What the Incorrect Mode Actually Does)
         * 您构建的那个“不符合要求”的查询模式，在 Elasticsearch 中被称为 混合搜索 (Hybrid Search)。它的工作流程如下：
         *
         * 并行执行两个查询：
         *
         * k-NN 查询部分：ES 在全部文档上运行向量相似度搜索，找出与查询向量最相似的 k 个结果，并为它们计算一个向量相关性得分。
         *
         * Bool 查询部分：ES 同时在全部文档上运行布尔查询（在您的情况下是 must_not 排除ID），找出符合条件的文档，并为它们计算一个传统的文本相关性得分（如 BM25，尽管在您的例子中得分可能为0）。
         *
         * 合并和重排结果 (Rescoring)：
         *
         * ES 获取来自上述两个查询的两组结果。
         *
         * 它使用一种融合算法（如 RRF - Reciprocal Rank Fusion）来合并这两组结果，综合考虑两种得分，生成一个最终的、重新排序的列表返回给用户。
         *
         * 结论就是： 在这种模式下，must_not 过滤器仅仅影响了布尔查询那一路的结果，但完全没有对 k-NN 查询本身起到任何过滤作用。
         * k-NN 搜索依然我行我素地在全量数据中寻找最近邻，所以它当然有可能找回那些您想排除的 ID。
         *
         * 四、解决方案 (Solution)
         * 正确的做法是，明确告诉 Elasticsearch：“请先应用我的过滤条件，然后在满足条件的文档子集上，再执行 k-NN 搜索。”
         *
         * 这需要将布尔过滤器作为 knn 查询的一个内部参数，而不是一个平级的查询。
         *
         * 正确的实现步骤：
         *
         * 将过滤逻辑构建成一个独立的 Query 对象。
         *
         * Java
         *
         * Query filterQuery = Query.of(q -> q
         *     .bool(b -> b
         *         .mustNot(m -> m
         *             .ids(i -> i.values(excludeContentIdListStr))
         *         )
         *     )
         * );
         * 在 knn() 方法的 lambda 表达式内部，调用 filter() 方法，并将上面创建的 filterQuery 作为参数传入。
         * filter() 方法是属于 KnnSearch.Builder 的，而不是 SearchRequest.Builder 的。
         *
         * 正确的代码结构示意：
         *
         * Java
         *
         * // builder 是 SearchRequest.Builder
         * builder.knn(k -> k  // k 是 KnnSearch.Builder
         *         .field(...)
         *         .queryVector(...)
         *         .k(...)
         *         // 关键在这里：将 filter 作为 knn 查询的一个参数
         *         .filter(filterQuery)
         * );
         * 通过这种方式，Elasticsearch 就会按照预期的“先过滤，后搜索”的逻辑执行，从而返回精确、干净且符合去重要求的推荐结果。
         */
        SearchRequest.Builder builder = new SearchRequest.Builder();

        // 我们要构建的布尔过滤查询
        Query filterQuery = Query.of(q -> q
                .bool(b -> b
                        .mustNot(m -> m
                                .ids(i -> i
                                        .values(excludeContentIdList)
                                )
                        )
                )
        );

        builder.index(indexName)
                .knn(k -> k
                        .field("average_vector")
                        .queryVector(queryVector)
                        .k(similarContentNumber)
                        .numCandidates(Math.max(similarContentNumber * 2, 100))// 通常 numCandidates 应该是k的倍数
                        .similarity(UserRecommendSearchMiniSimilarity)
                        .filter(filterQuery))
                        //只返回 contentParent_id字段，减少网络传输
                        .source(s->s.filter(src->src.includes("id")));

        // 使用获取到的向量进行相似性搜索
        SearchRequest searchRequest = builder.build();

        SearchResponse<Map> searchResponse = client.search(searchRequest, Map.class);

        List<Long> similarContentIds = new ArrayList<>();
        for (Hit<Map> hit : searchResponse.hits().hits()) {
            similarContentIds.add(Long.parseLong(hit.id()));
        }
//        /**
//         * 删除相似内容列表中当前帖子或者视频的id
//         */
//        similarContentIds.removeIf(similarContentId -> similarContentId.equals(id));

        return similarContentIds;
    }



    public List<float[]> multiGetVectorByIds(List<Long> contentIdList,String indexName) throws IOException {
        if (contentIdList == null || contentIdList.isEmpty()) {
            float[] zeroVector = new float[ModelConfig.DimensionOfEmbeddingModel];
            List<float[]> newUserVectorList = new ArrayList<>();
            newUserVectorList.add(zeroVector);
            return newUserVectorList;
        }

        // 1. 将 List<Long> 转换为 List<String>
        List<String> idStringList = contentIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 2. 构建 MgetRequest
        MgetRequest mgetRequest = MgetRequest.of(m -> m
                .index(indexName)
                .ids(idStringList)
                // 指定只返回向量字段，减少网络传输
                .sourceIncludes("average_vector")
        );
        return executeMultiRequest(mgetRequest, VectorDoc.class,VectorDoc::getAverage_vector);

        /**
         * 存储向量的代码如下:
         *
         * float[] average_vector = new float[]{0.1f, 0.2f, ...};
         * data.put("average_vector", average_vector);
         *
         * ES Java客户端 (序列化): 当调用.document(data)时，客户端内部的JSON库（通常是Jackson）会将的data Map转换为JSON字符串。在这个过程中，float[] 被转换成了一个JSON数组。
         *
         * JSON
         *
         * {
         *   "average_vector": [0.10000000149011612, 0.20000000298023224, ...]
         * }
         * Elasticsearch (存储与响应): ES接收并存储这个JSON。当您查询时，它会将这个JSON原样返回给的客户端。
         *
         * ES Java客户端 (反序列化): 这是最关键的一步！客户端收到了包含"average_vector": [ ... ]的JSON。
         * 由于我们指定了用 VectorDoc.class来接收结果，所以客户端会把JSON对象反序列化为一个VectorDoc。
         * 
         * 如果你的ElasticSearch的向量索引部分的名字叫做 average_vector
         * 确保VectorDoc.class里面的 float[] 名字也叫 average_vector , ES 返回的包含 average_vector 的字段名字的JSON要反序列化成一个float[],
         * 所以确保用作接收结果的类的 float[] 也叫做 average_vector
         */
    }

    /**
     * 执行多个get查询请求, 并返回结果
     * @param mgetRequest
     * @param clazz
     * @param extractor
     * @return
     * @param <T>
     * @param <R>
     * @throws IOException
     */
    public <T, R> List<R> executeMultiRequest(
            MgetRequest mgetRequest,
            Class<T> clazz,
            Function<T, R> extractor) throws IOException {

        MgetResponse<T> mgetResponse = client.mget(mgetRequest, clazz);

        List<R> resultList = new ArrayList<>();

        for (MultiGetResponseItem<T> doc : mgetResponse.docs()) {
            if (doc.result().found()) {
                T source = doc.result().source();
                R extractedValue = extractor.apply(source);
                if (extractedValue != null) {
                    resultList.add(extractedValue);
                }
            }
        }

        return resultList;
    }

    /**
     * @param keyword
     * @param contentType
     * @param textSearchFields
     * @return
     * @throws IOException
     */
    public List<String> hybridSearch(String keyword, ContentType contentType, List<String> textSearchFields) throws IOException {
        // Step 1: 将用户关键词转换为向量，用于后续的 KNN 搜索
        float[] queryVectorArray = embeddingService.getEmbedding(keyword);
        List<Float> queryVector = new ArrayList<>();
        for (float f : queryVectorArray) {
            queryVector.add(f);
        }

        // 执行基于关键词的全文检索（BM25），返回匹配的文本块
        SearchResponse<Map> textSearchResponse = performTextSearch(keyword, contentType.getType(), textSearchFields);

        // 执行基于向量的 KNN 搜索，返回语义相似的文本块
        SearchResponse<Map> knnSearchResponse = performKnnSearch(queryVector, contentType.getType());

        // 使用 RRF（Reciprocal Rank Fusion）算法融合两种搜索结果，
        // 得到按综合相关性排序的文本块 ID 列表（rankedChunkIds）
        List<String> rankedAwardIds = reciprocalRankMerge(textSearchResponse, knnSearchResponse, textWeight, knnWeight);

        return rankedAwardIds;
    }

    /**
     * 面向AI的“知识块检索”功能
     * 目标：AI根据用户问题，获取一个按相关性排序的文本块列表，作为生成答案的上下文。
     * @param keyword
     * @param contentType
     * @param textSearchFields
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> ragHybridSearch(String keyword,ContentType contentType, List<String> textSearchFields) throws IOException {
        float[] queryVectorArray = embeddingService.getEmbedding(keyword);
        List<Float> queryVector = new ArrayList<>();
        for (float f : queryVectorArray) {
            queryVector.add(f);
        }

        SearchResponse<Map> textSearchResponse = performTextSearch(keyword, contentType.getType(), textSearchFields);

        SearchResponse<Map> knnSearchResponse = performKnnSearch(queryVector, contentType.getType());

        List<String> rankedChunkIds = reciprocalRankMerge(textSearchResponse, knnSearchResponse, textWeight, knnWeight);

        Map<String, Map> allHitsMap = new HashMap<>();
        for (Hit<Map> hit : textSearchResponse.hits().hits()) {
            allHitsMap.putIfAbsent(hit.id(), hit.source());
        }
        for (Hit<Map> hit : knnSearchResponse.hits().hits()) {
            allHitsMap.putIfAbsent(hit.id(), hit.source());
        }
        List<Map<String,Object>> ragHybridSearchResultList = new ArrayList<>();
        for (String chunkId : rankedChunkIds) {
            ragHybridSearchResultList.add(allHitsMap.get(chunkId.toString()));
        }
        return ragHybridSearchResultList;
    }

    public SearchResponse<Map> performTextSearch(String keyword,String indexName, List<String> textSearchFields) throws IOException {
        // 2. 执行关键字搜索 (Text Search)
        Query textQuery = Query.of(q -> q
                .multiMatch(m -> m
                        .query(keyword)
                        .fields(textSearchFields)));

        SearchRequest textSearchRequest  = SearchRequest.of(s -> s
                .index(indexName)
                .query(textQuery)
                .size(resultLimit * 2));// 获取更多结果以便合并

        return client.search(textSearchRequest, Map.class);
    }

    public SearchResponse<Map> performKnnSearch(List<Float> queryVector,String indexName) throws IOException {
        // 3. 执行向量搜索 (KNN Search)
        SearchRequest knnSearchRequest = SearchRequest.of(s -> s
                .index(indexName)
                .knn(k -> k
                        .field("name_vector")
                        .queryVector(queryVector)
                        .k(10)
                        .numCandidates(100)
                        .similarity(RAGSearchMinSimilarity)
                )
                .size(resultLimit * 2) // 获取更多结果以便合并
        );
        return client.search(knnSearchRequest, Map.class);
    }


    public List<String> reciprocalRankMerge(
            SearchResponse<Map> textResponse,
            SearchResponse<Map> knnResponse,
            double textWeight,
            double knnWeight) {

        Map<String, Double> rrfScores = new HashMap<>();

        // 处理关键字搜索结果：按顺序分配 rank（从 1 开始）
        List<Hit<Map>> textHits = textResponse.hits().hits();
        for (int i = 0; i < textHits.size(); i++) {
            String id = textHits.get(i).id();
            int rank = i + 1; // rank 从 1 开始
            double rrf = textWeight * (1.0 / (RRF_CONSTANT + rank));
            rrfScores.merge(id, rrf, Double::sum); // 累加（支持重复ID）
        }

        // 处理向量搜索结果
        List<Hit<Map>> knnHits = knnResponse.hits().hits();
        for (int i = 0; i < knnHits.size(); i++) {
            String id = knnHits.get(i).id();
            int rank = i + 1;
            double rrf = knnWeight * (1.0 / (RRF_CONSTANT + rank));
            rrfScores.merge(id, rrf, Double::sum);
        }

        // 按 RRF 分数降序排序，返回 ID 列表
        return rrfScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 构建搜索请求，用于查找特定日期范围内的文档，支持排序和分页。
     *
     * @param startTime 开始日期，格式为 "yyyy-MM-dd"。
     * @param endTime 结束日期，格式为 "yyyy-MM-dd"。
     * @param page 分页的页码。
     * @param size 每页结果数量。
     * @param indexName 要搜索的索引名称。
     * @param sortOrder 'createdAt' 字段的排序顺序（升序或降序）。
     * @return 配置好的SearchRequest对象。
     */
    public SearchRequest getSearchByTimeRequest(String startTime, String endTime, int page, int size, String indexName, SortOrder sortOrder) {
        LocalDate startDate = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 转换为LocalDateTime并格式化为字符串，以匹配ES中的存储格式。
        String startDateTime = startDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String endDateTime = endDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q
                        .range(r -> r
                                .date(t->t
                                        .gte(startDateTime)
                                        .lte(startDateTime)
                                )
                        )
                )
                .sort(s -> s
                        .field(f -> f
                                .field("createdAt")
                                .order(sortOrder)
                        )
                )
                .from(page * size)
                .size(size)
                .build();
    }

    /**
     * 执行搜索请求并从响应中提取文档ID。
     *
     * @param searchRequest 要执行的搜索请求。
     * @return 文档ID列表，以Long类型表示。
     * @throws IOException 如果在搜索操作期间发生I/O错误。
     */
    public List<Long> getIds(SearchRequest searchRequest) throws IOException {
        // 我们使用Object.class因为我们不需要反序列化源数据，只需要元数据（_id）。
        SearchResponse<Object> searchResponse = client.search(searchRequest, Object.class);

        List<Hit<Object>> hits = searchResponse.hits().hits();
        List<Long> ids = new ArrayList<>();
        for (Hit<Object> hit : hits) {
            ids.add(Long.parseLong(hit.id()));
        }
        return ids;
    }
}