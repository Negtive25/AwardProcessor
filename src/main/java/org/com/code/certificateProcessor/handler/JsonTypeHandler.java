package org.com.code.certificateProcessor.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class JsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    // Jackson 提供的 JSON 工具
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Java -> MySQL 时调用
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        // 把 Map 转成 JSON 字符串存入数据库
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // MySQL -> Java 时调用
    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        // 把 JSON 字符串解析成 Map
        try {
            return json == null ? null : mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Map.of();
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Map.of();
    }

    public JsonTypeHandler() {
        super();
    }

    @Override
    public void setConfiguration(Configuration c) {
        super.setConfiguration(c);
    }

    /**
     * 如果参数 parameter 是 null：我明确知道这个 Map 最终是存为JSON字符串的，所以当它是 null 时，
     * 我不要依赖不确定的 jdbcType，我明确告诉数据库，请将这个 null 设置为 java.sql.Types.VARCHAR（即SQL的字符串类型）。
     *
     * 如果参数 parameter 不是 null：那它肯定是一个 Map（因为MyBatis是根据类型才找到我的），
     * 所以请调用我写的 setNonNullParameter，把它序列化成JSON字符串。
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        // 直接调用setNonNullParameter来处理参数
        if (parameter == null) {
            ps.setNull(i, java.sql.Types.VARCHAR);
        } else {
            setNonNullParameter(ps, i, parameter, jdbcType);
        }
    }

    @Override
    public Map<String, Object> getResult(ResultSet rs, String columnName) throws SQLException {
        return super.getResult(rs, columnName);
    }

    @Override
    public Map<String, Object> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getResult(rs, columnIndex);
    }

    @Override
    public Map<String, Object> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getResult(cs, columnIndex);
    }
}
