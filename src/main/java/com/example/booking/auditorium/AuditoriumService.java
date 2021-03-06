package com.example.booking.auditorium;

import com.example.booking.exception.AuditoriumNotFoundException;
import com.example.booking.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriumService {

    private final AuditoriumRepository repository;

    @Autowired
    public AuditoriumService(AuditoriumRepository repository) {
        this.repository = repository;
    }

    public List<Auditorium> getAuditoriums() {
        return this.repository.findAll();
    }

    public Auditorium getAuditoriumById(Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new AuditoriumNotFoundException(
                    "Auditorium with Id " + id + " does not exists")
        );
    }

    public Auditorium addNewAuditorium(Auditorium auditorium) {
        return this.repository.save(auditorium);
    }

    public void deleteAuditorium(Long id) {
        boolean isExist = this.repository.existsById(id);

        if(!isExist){
            throw new BadRequestException(
                    "Auditorium with Id " + id + " does not exists");
        }

        this.repository.deleteById(id);

    }

    public Auditorium updateAuditorium(Long id, AuditoriumDto auditoriumDto) {

        Auditorium auditorium = this.repository.findById(id).orElseThrow(
                ()-> new BadRequestException("Auditorium with Id " + id + " does not exists"));

        Integer numberOfSeats = auditoriumDto.getNumberOfSeats();

        if(numberOfSeats != null && numberOfSeats > 0) {
            auditorium.setNumberOfSeats(numberOfSeats);
        }

        return this.repository.save(auditorium);

    }
}
