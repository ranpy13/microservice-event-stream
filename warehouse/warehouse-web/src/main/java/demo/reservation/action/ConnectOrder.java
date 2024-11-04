package demo.reservation.action;

import demo.domain.Action;
import demo.reservation.domain.Reservation;
import demo.reservation.domain.ReservationModule;
import demo.reservation.domain.ReservationService;
import demo.reservation.domain.ReservationStatus;
import demo.reservation.event.ReservationEvent;
import demo.reservation.event.ReservationEventType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Connects an {@link Reservation} to an Order.
 *
 * @author Kenny Bastani
 */
@Service
@Transactional
public class ConnectOrder extends Action<Reservation> {
    private final Logger log = Logger.getLogger(this.getClass());

    public Reservation apply(Reservation reservation, Long orderId) {
        Assert.isTrue(reservation
                .getStatus() == ReservationStatus.RESERVATION_CREATED, "Reservation must be in a created state");

        ReservationService reservationService = reservation.getModule(ReservationModule.class).getDefaultService();

        // Connect the order
        reservation.setOrderId(orderId);
        reservation.setStatus(ReservationStatus.ORDER_CONNECTED);
        reservation = reservationService.update(reservation);

        try {
            // Trigger the order connected event
            reservation.sendAsyncEvent(new ReservationEvent(ReservationEventType.ORDER_CONNECTED, reservation));
        } catch (Exception ex) {
            log.error("Could not connect reservation to order", ex);
            reservation.setOrderId(null);
            reservation.setStatus(ReservationStatus.RESERVATION_CREATED);
            reservation = reservationService.update(reservation);
        }

        return reservation;
    }
}
