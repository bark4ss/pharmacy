package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.UserData;
import com.example.pharmacy.entity.UserRole;
import com.example.pharmacy.entity.UserWithoutPassword;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AllUsersCommand implements Command {
    private static final String ALL_USERS_MAP = "allUsersMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_ALL_USERS_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);

        Map<UserWithoutPassword, UserData> allUsersMap = new HashMap<>();
        try {
            List<UserWithoutPassword> allUsers = UserService.getInstance().findAllUsers();
            for (UserWithoutPassword user : allUsers) {
                if (UserRole.USER == user.getRole()) {
                    Optional<UserData> optionalUserData = UserService.getInstance().findUserDataByUserId(user.getUserId());
                    if (optionalUserData.isPresent()) {
                        UserData userData = optionalUserData.get();
                        allUsersMap.put(user, userData);
                    }
                } else {
                    allUsersMap.put(user, UserData.builder().build());
                }
            }
            request.setAttribute(ALL_USERS_MAP, allUsersMap);
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
