package com.example.blog.repository;

import com.example.blog.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
    Optional<SysUser> findFirstByNickname(String nickname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
