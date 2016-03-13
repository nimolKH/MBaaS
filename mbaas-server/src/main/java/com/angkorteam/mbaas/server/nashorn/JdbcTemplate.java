package com.angkorteam.mbaas.server.nashorn;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/13/16.
 */
public class JdbcTemplate {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForMap(sql, paramMap);
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForList(sql, paramMap);
    }

    public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
        audit(sql);
        return this.jdbcTemplate.update(sql, paramMap);
    }

    public int[] batchUpdate(String sql, Map<String, ?>[] batchValues) throws DataAccessException {
        audit(sql);
        return this.jdbcTemplate.batchUpdate(sql, batchValues);
    }

    private void audit(String sql) {
        String sqlTrimed = sql.trim();
        if (sqlTrimed.substring(0, "drop".length()).toLowerCase().equals("drop")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "create".length()).toLowerCase().equals("create")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "alter".length()).toLowerCase().equals("alter")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "call".length()).toLowerCase().equals("call")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "kill".length()).toLowerCase().equals("kill")) {
            throw new DataAccessResourceFailureException(sql);
        }
    }
}