package com.studerw.activiti.alert;

import com.google.common.collect.ImmutableMap;
import com.studerw.activiti.model.Alert;
import com.studerw.activiti.web.PagingCriteria;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author William Studer
 * Date: 5/21/14
 */
@Repository
@Component("alertDao")
public class JdbcAlertDao implements AlertDao {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcAlertDao.class);

    protected DataSource ds;
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired @Qualifier("dataSource")
    public void setDataSource(DataSource datasource) {
        this.ds = datasource;
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(this.ds);
    }


    @Override @Transactional
    public String create(Alert alert) {
        LOG.debug("Inserting alert into SQL backend: {}", alert);
        checkArgument(StringUtils.isBlank(alert.getId()), "alert id cannot be already set");

        String id = UUID.randomUUID().toString();
        alert.setId(id);
        String sql = "INSERT INTO Alert (id, created_by, message, priority, user_id,  acknowledged, created_date) " +
                "VALUES (:id, :createdBy, :message, :priority, :userId, :acknowledged, :createdDate)";

        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(alert);
        int results = this.namedJdbcTemplate.update(sql, source);
        LOG.debug("Got: {} results", results);

        return id;
    }

    @Override
    public void createWithId(Alert obj) {
        throw new UnsupportedOperationException("not supported");
    }


    @Override
    public Alert read(String alertId) {
        String sql = "SELECT * FROM Alert where id = :id";
        Map<String, String> params = ImmutableMap.of("id", alertId);
        Alert alert = this.namedJdbcTemplate.queryForObject(sql, params, new AlertRowMapper());
        return alert;
    }

    @Override
    public List<Alert> readAll() {
        String sql = "SELECT * FROM Alert ORDER BY created_date ASC";
        List<Alert> alerts = this.namedJdbcTemplate.query(sql, new AlertRowMapper());
        LOG.debug("got all alerts: {}", alerts.size());
        return alerts;
    }

    @Override
    public int getCount() {
        String sql = "SELECT count(*) FROM Alert";
        @SuppressWarnings("unchecked")
        int count = this.namedJdbcTemplate.queryForObject(sql, Collections.EMPTY_MAP, Integer.class);
        LOG.debug("Got count: {} of Alerts", count);
        return count;
    }


    @Override
    public void delete(String alertId) {
        String sql = "DELETE FROM Alert WHERE id = :id";
        Map<String, String> params = ImmutableMap.of("id", alertId);
        int deleted = this.namedJdbcTemplate.update(sql, params);
        LOG.debug("Deleted: {} alerts", deleted);
    }


    @Override
    public void update(Alert alert) {
        checkArgument(StringUtils.isNotBlank(alert.getId()), "alert id cannot be blank");
        String sql = "UPDATE Alert SET created_by=:createdBy, user_id=:userId, message=:message, priority=:priority," +
                " acknowledged=:acknowledged  WHERE id=:id";

        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(alert);
        int results = this.namedJdbcTemplate.update(sql, source);
        LOG.debug("Updated: {} alerts", results);
    }

    @Override
    public List<Alert> readPage(PagingCriteria criteria) {
        LOG.debug("reading page with criteria: {}", criteria);
        if (criteria == null || criteria.getLimit() == null || criteria.getStart() == null) {
            LOG.warn("criteria invalid - reading all instead of subset");
            return readAll();
        }
        String sql = "SELECT LIMIT :start :limit * FROM Alert ORDER BY created_date ASC";
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(criteria);

        List<Alert> alerts = this.namedJdbcTemplate.query(sql, source, new AlertRowMapper());
        LOG.debug("{} alerts returned using criteria: {}", alerts.size(), criteria);

        return alerts;
    }

    @Override
    public List<Alert> readActiveAlertsByUserId(String userId) {
        String sql = "SELECT * FROM Alert WHERE user_id = :userId AND acknowledged = FALSE ORDER BY created_date ASC";
        Map<String, String> params = ImmutableMap.of("userId", userId);
        List<Alert> alerts = this.namedJdbcTemplate.query(sql, params, new AlertRowMapper());
        LOG.debug("got {} active alerts for user {}: ", alerts.size(), userId);
        return alerts;
    }

    @Override
    public void acknowledgeAlert(String alertId) {
        String sql = "UPDATE Alert SET acknowledged=:acknowledged  WHERE id=:id";
        Map<String, Boolean> params = ImmutableMap.of("acknowledged", Boolean.TRUE);
        int results = this.namedJdbcTemplate.update(sql, params);
        LOG.debug("Updated: {} alerts", results);
    }
}
