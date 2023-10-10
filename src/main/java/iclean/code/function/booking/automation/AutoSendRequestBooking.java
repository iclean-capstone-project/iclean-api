package iclean.code.function.booking.automation;

import iclean.code.data.domain.Booking;
import iclean.code.data.domain.User;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class AutoSendRequestBooking {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    //1h
    @Scheduled(fixedRate = 3600000L)
    public void jobMapForManager() {
        try {
            List<Booking> bookings = bookingRepository.findAllWithNoManager();
            List<User> managers = userRepository.findAllManager();
            if (bookings.isEmpty()) {
                log.info(Utils.getDateTimeNowAsString() + " ----> No booking at this time!");
                return;
            }
            if (managers.isEmpty()) {
                log.warn(Utils.getDateTimeNowAsString() + " ----> No manager at this time!");
                return;
            }

            int countManager = managers.size();
            int i = 0;
            for (Booking booking :
                    bookings
            ) {
                booking.setManager(managers.get(i++));
                if (i == countManager) i = 0;
            }

            bookingRepository.saveAll(bookings);

            log.info(Utils.getDateTimeNowAsString() + " ----> Set Manager successful!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
