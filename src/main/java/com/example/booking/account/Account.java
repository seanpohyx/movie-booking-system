package com.example.booking.account;

import com.example.booking.seatBooking.SeatBooking;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class Account {
    @Id
    @SequenceGenerator(
            name="account_sequence",
            sequenceName = "account_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_sequence"
    )
    private long accountId;

    private String name;

    private String email;

    private String password;

    private long createdTime;

    @OneToMany()
    @JoinColumn(name = "account")
    private List<SeatBooking> seatBookingList;
}
