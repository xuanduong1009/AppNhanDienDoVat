package com.example.vovbacsi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Calendar;

public class DoctorListItemLayout extends AppCompatActivity {
    private RecyclerView doctorRecyclerView;
    private ArrayList<Doctor> doctorList;
    private DoctorAdapter doctorAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list_item_layout);

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(this, doctorList);
        doctorRecyclerView.setAdapter(doctorAdapter);

        loadDoctors();
    }

    private void loadDoctors() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("doctors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                doctorList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        doctorList.add(doctor);
                    }
                }
                doctorAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DoctorListItemLayout.this, "Lỗi khi tải danh sách bác sĩ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onBookAppointmentClick(View view, Doctor doctor) {
        // Mở DatePicker và TimePicker để chọn thời gian
        showDatePickerDialog(doctor);
    }

    private void showDatePickerDialog(Doctor doctor) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Hiển thị TimePicker sau khi chọn ngày
                    showTimePickerDialog(doctor, year, month, dayOfMonth);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Doctor doctor, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // Cập nhật thời gian lịch hẹn
            String appointmentTime = year + "-" + (month + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute;
            saveAppointment(doctor, appointmentTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void saveAppointment(Doctor doctor, String appointmentTime) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        String appointmentId = databaseReference.push().getKey();

        Appointment appointment = new Appointment(doctor.getName(), doctor.getSpecialization(), appointmentTime);

        if (appointmentId != null) {
            databaseReference.child(appointmentId).setValue(appointment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DoctorListItemLayout.this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DoctorListItemLayout.this, "Đặt lịch thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
