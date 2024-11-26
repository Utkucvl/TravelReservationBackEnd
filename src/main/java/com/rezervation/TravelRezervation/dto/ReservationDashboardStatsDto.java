package com.rezervation.TravelRezervation.dto;

import lombok.Data;

@Data
public class ReservationDashboardStatsDto {
    private int totalSalesMonthly;
    private int totalSalesWeekly;
    private int numOfReservationsMonthly;
    private int numOfReservationsWeekly;
}
