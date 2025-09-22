package com.example.bankcards.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
}
