package com.example.spring_reactive.blockingtoreactivity;

import java.util.List;

public interface BlockingRepository<User> {

    List<User> findAll();

    User save(User user);
}
