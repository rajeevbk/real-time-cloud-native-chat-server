package com.rajeevbk.repository;

import com.rajeevbk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);
    Optional<User> findByKcUserId(final String id);
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.username LIKE %:searchTerm%")
    List<User> searchByTerm(@Param("searchTerm") String searchTerm);

    List<User> findByUsernameIn(List<String> usernames);

}
