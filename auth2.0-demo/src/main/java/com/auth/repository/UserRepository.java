package com.auth.repository;
import com.auth.entity.User;
import com.auth.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User> {

    User findUserByAccount(String account);
}