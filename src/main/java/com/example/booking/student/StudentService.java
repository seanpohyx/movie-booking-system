package com.example.booking.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents(){
        return this.studentRepository.findAll();
    }

    public void addNewStudent(Student student) {

        Optional<Student> studentByEmail = this.studentRepository.findStudentByEmail(student.getEmail());

        if(studentByEmail.isPresent()){
            throw new IllegalStateException("email taken");
        }

        this.studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        boolean exits = this.studentRepository.existsById(id);

        if(!exits){
            throw new IllegalStateException(
                    "student with Id " + id + "does not exists");
        }

        this.studentRepository.deleteById(id);
    }

    @Transactional
    public void updateStudent(Long id, String name, String email) {

        Student student = this.studentRepository.findStudentById(id).orElseThrow(
                ()-> new IllegalStateException("student with Id " + id + "does not exists"));

        if(name!=null &&
                name.length() > 0 &&
                !Objects.equals(student.getName(), name)){
            student.setName(name);
        }

        if(email!=null &&
                email.length() > 0 &&
                !Objects.equals(student.getEmail(), email)){

            Optional<Student> studentByEmail = this.studentRepository.findStudentByEmail(email);

            if(studentByEmail.isPresent()){
                throw new IllegalStateException("Email taken");
            }

            student.setEmail(email);
        }

        this.studentRepository.save(student);

    }
}
