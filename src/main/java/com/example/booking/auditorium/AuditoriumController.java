package com.example.booking.auditorium;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="api/v1/auditorium")
public class AuditoriumController {

    private final AuditoriumService service;
    private final ModelMapper modelMapper;

    @Autowired
    public AuditoriumController(AuditoriumService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path="{auditoriumId}")
    public ResponseEntity<AuditoriumDto> getAuditoriumById(@PathVariable("auditoriumId") Long id){
        return ResponseEntity.ok().body(convertToDTO(this.service.getAuditoriumById(id)));
    }

    @GetMapping
    public ResponseEntity<List<AuditoriumDto>> getAuditorium(){
        return ResponseEntity.ok().body(
                this.service.getAuditorium().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AuditoriumDto> addAuditorium(@RequestBody AuditoriumDto postDto){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auditorium").toUriString());
        return ResponseEntity.created(uri).body(convertToDTO(this.service.addNewAuditorium(convertToEntity(postDto))));
    }

    @DeleteMapping(path = "{auditoriumId}")
    public void removeAuditorium(@PathVariable("auditoriumId") Long id){
        this.service.deleteAuditorium(id);
    }

    @PutMapping(path="{auditoriumId}")
    public ResponseEntity<AuditoriumDto> updateAuditorium(
            @PathVariable("auditoriumId") Long id,
            @RequestParam(required = true) Integer numberOfSeats) {
        try {
            return ResponseEntity.ok().body(convertToDTO(this.service.updateAuditorium(id, numberOfSeats)));
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    public Auditorium convertToEntity(AuditoriumDto postDTO){
        return this.modelMapper.map(postDTO, Auditorium.class);
    }

    public AuditoriumDto convertToDTO(Auditorium auditorium){
        return this.modelMapper.map(auditorium, AuditoriumDto.class);
    }
}
