package com.example.vovbacsi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private ArrayList<Doctor> doctorList;
    private Context context;

    public DoctorAdapter(Context context, ArrayList<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item_layout, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Picasso.get().load(doctor.getImageUrl()).into(holder.imageView);
        } else {
            Picasso.get().load(R.drawable.baseline_person_24).into(holder.imageView);
        }

        holder.nameTextView.setText(doctor.getName());
        holder.specializationTextView.setText(doctor.getSpecialization());
        holder.degreeTextView.setText(doctor.getDegree());
        holder.birthYearTextView.setText(doctor.getBirthYear());
        holder.departmentTextView.setText(doctor.getDepartment());
        holder.specialtyTextView.setText(doctor.getSpecialty());
        holder.treatableDiseasesTextView.setText(doctor.getTreatableDiseases());
        holder.priceTextView.setText(String.format("$%.2f", doctor.getPrice()));

        // Set listener cho button Đặt lịch khám
        holder.bookAppointmentButton.setOnClickListener(v -> {
            if (context instanceof DoctorListItemLayout) {
                ((DoctorListItemLayout) context).onBookAppointmentClick(v, doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, specializationTextView, degreeTextView, birthYearTextView;
        TextView departmentTextView, specialtyTextView, treatableDiseasesTextView, priceTextView;
        Button bookAppointmentButton;

        DoctorViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.doctorImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            specializationTextView = itemView.findViewById(R.id.specializationTextView);
            degreeTextView = itemView.findViewById(R.id.degreeTextView);
            birthYearTextView = itemView.findViewById(R.id.birthYearTextView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            specialtyTextView = itemView.findViewById(R.id.specialtyTextView);
            treatableDiseasesTextView = itemView.findViewById(R.id.treatableDiseasesTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            bookAppointmentButton = itemView.findViewById(R.id.bookAppointmentButton);
        }
    }
}
