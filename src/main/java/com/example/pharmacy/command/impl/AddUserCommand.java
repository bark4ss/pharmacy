package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.UserRole;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.UserService;
import com.example.pharmacy.util.PasswordEncoder;
import com.example.pharmacy.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("Duplicates")
public class AddUserCommand implements Command {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String ROLE = "role";
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "userSavedSuccess";
    private static final String IF_ERROR_MESSAGE = "message";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        UserRole role = UserRole.valueOf(request.getParameter(ROLE));
        String login = request.getParameter(LOGIN);
        String password = request.getParameter(PASSWORD);
        try {
            if (UserValidator.isValidLogin(login) &&
                    UserValidator.isValidPassword(password) &&
                    !UserService.getInstance().isUsernameExist(login)) {
                UserService.getInstance().saveUser(login, PasswordEncoder.encodePassword(password), role);
                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
            } else {
                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
