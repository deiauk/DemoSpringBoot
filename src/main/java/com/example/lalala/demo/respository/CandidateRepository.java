package com.example.lalala.demo.respository;

import com.example.lalala.demo.model.Candidate;
import com.example.lalala.demo.model.Item;
import com.example.lalala.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Candidate findByItemAndUser(Item item, User user);
}