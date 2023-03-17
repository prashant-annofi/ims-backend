package com.annofi.ims.repository;

import com.annofi.ims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	User findTop1ByOrderByIdAsc();
	User findByUsername(String username);
	User findByUsernameAndDeleted(String username, Boolean Deleted);
	User findByUsernameAndDeletedFalse(String username);
	User findByUsernameAndDeletedTrue(String username);
	
	@Query("UPDATE User u SET u.failedAttempt = :#{#failAttempts} WHERE u.username = :#{#username} ")
    @Modifying
    public void updateFailedAttempts(@Param("failAttempts") int failAttempts, @Param("username") String username);
}
