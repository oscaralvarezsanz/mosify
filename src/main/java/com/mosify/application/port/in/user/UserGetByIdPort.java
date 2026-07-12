package com.mosify.application.port.in.user;

import com.mosify.domain.model.User;
import java.util.UUID;

public interface UserGetByIdPort {
    User getUserById(UUID id);
}
