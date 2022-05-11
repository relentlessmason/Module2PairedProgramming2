package com.techelevator.dao;

import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> siteList = new ArrayList<>();
        String sql = "SELECT * FROM site JOIN campground USING (campground_id)" +
                "JOIN park USING (park_id) WHERE max_rv_length > 0 AND park_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()) {
            siteList.add(mapRowToSite(results));
        }
        return siteList;
    }

    @Override
    public List<Site> getAvailableSites(int parkId) {
        List<Site> siteList = new ArrayList<>();
        String sql = "SELECT * FROM site LEFT JOIN reservation USING (site_id) JOIN campground " +
                "USING (campground_id) JOIN park USING (park_id) WHERE reservation_id IS NULL AND park_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()) {
            siteList.add(mapRowToSite(results));
        }
        return siteList;
    }

    @Override
    public List<Site> getFutureReservations(int parkId, LocalDate startDate, LocalDate endDate) {
        List<Site> futureSites = new ArrayList<>();
        String sql = "SELECT * FROM site JOIN campground USING (campground_id) WHERE park_id = ? AND site_id NOT IN " +
        "(SELECT site_id FROM reservation WHERE (? BETWEEN from_date AND to_date AND ? BETWEEN from_date AND to_date))";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId, startDate, endDate);
        while(results.next()) {
            futureSites.add(mapRowToSite(results));
        }
        return futureSites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
