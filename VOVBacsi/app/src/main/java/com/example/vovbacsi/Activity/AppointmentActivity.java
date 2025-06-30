package com.example.vovbacsi.Activity;

import android.os.Bundle;
import android.widget.Toast;
import com.example.vovbacsi.R;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {

    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String FCM_SERVER_KEY = "YOUR_SERVER_KEY"; // Thay thế bằng Server Key của bạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Giả sử bạn đã có thông tin bác sĩ và thời gian hẹn trong các biến sau
        String doctorName = "Dr. John Doe"; // Tên bác sĩ
        String appointmentTime = "12:30 PM, 25/11/2024"; // Thời gian lịch hẹn

        // Gọi phương thức gửi thông báo
        sendNotificationToDoctor(doctorName, appointmentTime);
    }

    public void sendNotificationToDoctor(String doctorName, String appointmentTime) {
        // Lấy FCM token của bác sĩ từ Firebase
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");
        doctorsRef.orderByChild("doctorName").equalTo(doctorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    String fcmToken = doctorSnapshot.child("fcm_token").getValue(String.class);
                    if (fcmToken != null) {
                        sendFCMNotification(fcmToken, doctorName, appointmentTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(AppointmentActivity.this, "Lỗi khi lấy thông tin bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendFCMNotification(String fcmToken, String doctorName, String appointmentTime) {
        String notificationTitle = "Lịch hẹn mới";
        String notificationBody = "Bạn có lịch hẹn mới vào lúc " + appointmentTime + " từ bệnh nhân.";

        JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("to", fcmToken);
            notificationData.put("notification", new JSONObject()
                    .put("title", notificationTitle)
                    .put("body", notificationBody)
            );

            // Gửi yêu cầu HTTP đến FCM
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API_URL, notificationData,
                    response -> {
                        // Xử lý phản hồi từ FCM
                        Toast.makeText(AppointmentActivity.this, "Thông báo đã gửi thành công", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        // Xử lý lỗi
                        Toast.makeText(AppointmentActivity.this, "Lỗi khi gửi thông báo", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "key=" + FCM_SERVER_KEY);
                    return headers;
                }
            };

            // Thêm request vào Volley request queue
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AppointmentActivity.this, "Lỗi khi tạo thông báo", Toast.LENGTH_SHORT).show();
        }
    }
}
