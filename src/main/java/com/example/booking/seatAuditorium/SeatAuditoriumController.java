package com.example.booking.seatAuditorium;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="api/v1/seatAuditorium")
public class SeatAuditoriumController {

    private final SeatAuditoriumRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public SeatAuditoriumController(SeatAuditoriumRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public void getSeatAuditorium(){

    }
}
