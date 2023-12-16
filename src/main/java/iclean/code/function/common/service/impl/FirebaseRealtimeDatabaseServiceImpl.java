package iclean.code.function.common.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import iclean.code.function.common.service.FirebaseRealtimeDatabaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class FirebaseRealtimeDatabaseServiceImpl implements FirebaseRealtimeDatabaseService {
    @Override
    public void sendMessage(String key, String qrData) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookingDetailId");
        Map<String, Object> data = new HashMap<>();
        data.put("qrData", qrData);
        data.put("bookingDetailId", key);

        ref.push().setValueAsync(data);
    }

    @Override
    public void sendNotification(String phoneNumber, String bookingDetailId, String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificationBooking");
        Map<String, Object> data = new HashMap<>();
        data.put("phoneNumber", phoneNumber);
        data.put("message", message);

        ref.push().setValueAsync(data);
    }
}
