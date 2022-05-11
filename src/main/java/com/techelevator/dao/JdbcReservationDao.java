package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        Reservation reservation = new Reservation();
        reservation.setSiteId(siteId);
        reservation.setName(name);
        reservation.setFromDate(fromDate);
        reservation.setToDate(toDate);
        String sql = "INSERT INTO reservation(site_id, name, from_date, to_date)" +
                "VALUES(?,?,?,?) RETURNING reservation_id";
        int newId = jdbcTemplate.queryForObject(sql, int.class, reservation.getSiteId(), reservation.getName(),
                reservation.getFromDate(), reservation.getToDate());
        return newId;
    }

    @Override
    public List<Reservation> getUpcomingReservations(int parkId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation JOIN site USING (site_ID) JOIN campground USING " +
                "(campground_id) JOIN park USING (park_id) WHERE " +
                "(from_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30) AND (park_id = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()) {
            reservations.add(mapRowToReservation(results));
        }
        return reservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
