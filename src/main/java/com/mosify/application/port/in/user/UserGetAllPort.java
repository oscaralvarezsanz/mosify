package com.mosify.application.port.in.user;

import com.mosify.domain.model.User;
import java.util.List;

public interface UserGetAllPort {
    List<User> getAllUsers();
}
