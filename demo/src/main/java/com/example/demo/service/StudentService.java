package com.example.demo.service;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.specification.StudentSpecifications;

import jakarta.annotation.PostConstruct;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly=true)
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }
    //private List<Student> students = new ArrayList<>(Arrays.asList());
    //private AtomicLong IdGenerator = new AtomicLong();

    @PostConstruct
    public void unit(){}
    public List<Student> getAll(){
        return studentRepository.findAll();
    }
    @Cacheable(value="students", key="#root.MethodName")
    public List<Student> getAllByName(String name){
        return studentRepository.findAllByName(name);
    }
    @CacheEvict(value="students", allEntries=true)//нужно
    @Transactional
    public Student create(Student student){
        return studentRepository.save(student);
    }
    @Cacheable(value="students", key="#id")
    public Student getById(Long id) {//true
        return studentRepository.findById(id).orElse(null);
            
    }
     
    public Student update(Long id, Student student) {//true
        return studentRepository.findById(id).map(existingStudent -> {
            existingStudent.setName(student.getName());
            existingStudent.setGroupName(student.getGroupName());
            existingStudent.setRecentEntries(student.getRecentEntries());
            return studentRepository.save(existingStudent);
        }).orElse(null);
    }
    @Caching(evict={//нужнл
         @CacheEvict(value="students", allEntries=true),
         @CacheEvict(value={"students", "student"}, key="#id")
    })
    public boolean deleteById(Long id) {//true
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public Page<Student> getByFilter(String name, String groupName,  Pageable pageable){//переделать
        return  studentRepository.findAll(StudentSpecifications.filter(name, groupName), pageable);
    }
}
