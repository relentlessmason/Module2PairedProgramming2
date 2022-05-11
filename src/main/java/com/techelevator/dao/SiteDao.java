package com.techelevator.dao;

import com.techelevator.model.Site;

import java.time.LocalDate;
import java.util.List;

public interface SiteDao {

    List<Site> getSitesThatAllowRVs(int parkId);

    List<Site> getAvailableSites(int parkId);

    List<Site> getFutureReservations(int parkId, LocalDate startDate, LocalDate endDate);
}
