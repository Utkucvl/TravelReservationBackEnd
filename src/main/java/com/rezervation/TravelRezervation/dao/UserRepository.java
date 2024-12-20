package com.rezervation.TravelRezervation.dao;

import com.rezervation.TravelRezervation.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);
    User findByEmail(String email);
}
