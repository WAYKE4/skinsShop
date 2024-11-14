package com.boot.dbskins.Security.Service;

import com.boot.dbskins.Repository.UserRepository;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SchedulerService {

    private final UserSecurityRepository userSecurityRepository;
    private final UserService userService;

    @Autowired
    public SchedulerService(UserSecurityRepository userSecurityRepository, UserService userService) {
        this.userSecurityRepository = userSecurityRepository;
        this.userService = userService;
    }

    @Scheduled(cron = "${task.cron.expression}")
    public void scheduleCronExpressionParametrizedTask() {
        Optional<List<UserSecurity>> users = userSecurityRepository.findAllByActivationTokenIsNotNull();
        if (users.isPresent()) {
            for (UserSecurity user : users.get()) {
                log.info("Removing an unverified user " + user.getId() + ", Name: " + user.getUserLogin());
                userService.deleteUser(user.getUserId());
            }
        }
    }
}
