package com.rezervation.TravelRezervation.scheduling;

import com.rezervation.TravelRezervation.service.impl.ReservationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ReservationServiceImpl reservationService;

    @Scheduled(cron = "0 0 12 * * ?")// Her gece saat 12'de çalışır
    public void scheduleTaskUsingCronExpression() {
        reservationService.notifyUsersForNextWeekReservations();
        reservationService.notifyUsersForNextDayReservations();
    }
}
