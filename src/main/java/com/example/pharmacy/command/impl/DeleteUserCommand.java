package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.UserRole;
import com.example.pharmacy.entity.UserWithoutPassword;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class DeleteUserCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "userDeleteSuccess";
    private static final String IF_ERROR_MESSAGE = "userDeleteFailed";
    private static final String DELETED_USER_ID = "userId";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        Long userId = Long.valueOf(request.getParameter(DELETED_USER_ID));
        try {
            Optional<UserWithoutPassword> optionalUser = UserService.getInstance().findUserWithoutPasswordById(userId);
            if (optionalUser.isPresent()) {
                UserWithoutPassword user = optionalUser.get();
                if (UserRole.USER == user.getRole()) {
                    if (UserService.getInstance().deleteUser(userId)) {
                        request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                    } else {
                        request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                    }
                } else {
                    if (UserService.getInstance().deleteAdminOrDoctor(userId)) {
                        request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                    } else {
                        request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                    }
                }
            }
        } catch (ServiceException e) {
            throw new CommandException("Service layer error", e);
        }
        return commandResult;
    }
}
