package iclean.code.function.dashboard.service.impl;

import iclean.code.data.domain.Booking;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.dashboard.HomeResponse;
import iclean.code.data.dto.response.dashboard.TopEmployee;
import iclean.code.data.repository.BookingDetailHelperRepository;
import iclean.code.data.repository.BookingRepository;
import iclean.code.data.repository.ServiceRegistrationRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.function.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;

    @Autowired
    private BookingDetailHelperRepository bookingDetailHelperRepository;

    @Override
    public ResponseEntity<ResponseObject> homeDashboard() {
        int sumOfAllUserWithoutAdmin = userRepository.findAllUserWithoutAdmin().size();
        int sumOfNewUserInCurrentWeek = userRepository.findNewUserInCurrentWeek().size();
        int sumOfAllBooking = bookingRepository.findAll().size();
        int sumOfAllServiceRegistration = serviceRegistrationRepository.findAll().size();
        double getSumOfIncome = bookingRepository.getSumOfIncome();
        List<Object[]> temp = bookingDetailHelperRepository.findTopEmployeesInMonth();
        List<TopEmployee> TopEmployeesInMonth = new ArrayList<>();

        for (Object[] row : temp) {
            Integer helperInformationId = (Integer) row[0];
            String fullName = (String) row[1];
            BigInteger count = (BigInteger) row[2];

            TopEmployeesInMonth.add(new TopEmployee(helperInformationId, fullName, count.intValue()));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(),
                        "Tổng người dùng: ", new HomeResponse(sumOfAllUserWithoutAdmin
                        , sumOfNewUserInCurrentWeek
                        , sumOfAllBooking
                        , sumOfAllServiceRegistration
                        , getSumOfIncome
                        , TopEmployeesInMonth)));
    }


    @Override
    public ResponseEntity<ResponseObject> findBookingByDate(String time, String option) {

        if (Objects.isNull(time) && Objects.isNull(option)){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Tổng đơn hàng trong ngày: ", bookingRepository.getBookingInCurrentWeek()));
        }
        List<Booking> bookings = new ArrayList<>();
        switch (option.toLowerCase()){
            case "day":
                bookings = bookingRepository.getBookingByOrderDate(time);
                break;

            case "month":
                bookings = bookingRepository.getBookingByMoth(time);
                break;

            case "week":
                bookings = bookingRepository.getBookingInCurrentWeek();
                break;
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(),
                        "Tổng đơn hàng trong ngày: ", bookings));
    }
}
