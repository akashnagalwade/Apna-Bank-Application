package com.mindspark.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindspark.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.createdAt < ?1")
	List<User> findUsersCreatedAt(LocalDate date);

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.accountNumber = ?1")
	User findByAccountNumber(String accountNumber);

	List<User> findByFirstName(String firstName);

	User findUserByEmail(String email);

	boolean existsByAccountNumber(String accountNumber);
}
