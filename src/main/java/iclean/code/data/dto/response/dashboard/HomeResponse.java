package iclean.code.data.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HomeResponse {

    private Integer sumOfAllUserWithoutAdmin;

    private Integer sumOfNewUserInCurrentWeek;

    private Integer sumOfAllBooking;

    private Integer sumOfAllServiceRegistration;

    private Double getSumOfIncome;

    private List<TopEmployee> topEmployees;

}
