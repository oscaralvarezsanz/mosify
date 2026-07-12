package com.mosify.application.port.in.user;

import com.mosify.domain.model.User;

public interface UserCreatePort {
    User createUser(User user);
}
