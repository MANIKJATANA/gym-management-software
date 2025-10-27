package com.jatana.gymmembershipmanagemt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member {
    @Id
    private String memberId;

    private String firstName;
    private String lastName;
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;


    private String phoneNumber;
    private String email;

    private String address;

    private String memberStatus;

    private String photoUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;





}
