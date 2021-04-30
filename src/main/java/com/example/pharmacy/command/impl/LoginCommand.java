package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.*;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.UserService;
import com.example.pharmacy.util.PasswordEncoder;
import com.example.pharmacy.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class LoginCommand implements Command {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String SESSION_USER = "user";
    private static final String SESSION_CART = "cart";
    private static final String SESSION_USER_DATA = "userData";
    private static final String SESSION_USER_PAYMENT_DATA = "userPaymentData";
    private static final boolean FLAG = true;
    private static final String IF_ERROR_MESSAGE = "message";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        CommandResult commandResult = null;
        String page;
        String loginValue = request.getParameter(LOGIN);
        String passwordValue = request.getParameter(PASSWORD);
        try {
            if (UserValidator.isValidLogin(loginValue) &&
                    UserValidator.isValidPassword(passwordValue) &&
                    UserService.getInstance().isUserExist(loginValue, PasswordEncoder.encodePassword(passwordValue))) {

                Optional<UserRole> optionalUserRole = UserService.getInstance().getUserRoleByLogin(loginValue);
                Optional<UserWithoutPassword> userWithoutPassword = UserService.getInstance()
                        .findUserDTOByLoginAndPassword(loginValue, PasswordEncoder.encodePassword(passwordValue));

                if (optionalUserRole.isPresent() && userWithoutPassword.isPresent()) {
                    UserWithoutPassword user = userWithoutPassword.get();
                    UserRole userRole = optionalUserRole.get();
                    request.getSession().setAttribute(SESSION_USER, user);

                    switch (userRole) {
                        case USER:
                            page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.USER_MAIN_PAGE_KEY);
                            Cart cart = makeNewUserCart(user);
                            request.getSession().setAttribute(SESSION_CART, cart);
                            Optional<UserPaymentData> maybePaymentData = UserService.getInstance().getPaymentDataById(user.getUserId());
                            Optional<UserData> maybeUserData = UserService.getInstance().findUserDataByUserId(user.getUserId());
                            if (maybePaymentData.isPresent() && maybeUserData.isPresent()) {
                                request.getSession().setAttribute(SESSION_USER_DATA, maybeUserData.get());
                                request.getSession().setAttribute(SESSION_USER_PAYMENT_DATA, maybePaymentData.get());
                            }
                            break;
                        case DOCTOR:
                            page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.DOCTOR_MAIN_PAGE_KEY);
                            break;
                        case ADMIN:
                            page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
                            break;
                        default:
                            throw new EnumConstantNotPresentException(UserRole.class, userRole.name());
                    }
                    commandResult = new CommandResult(page, NavigationType.REDIRECT);
                }
            } else {
                page = PageManager.getPageURI(PageMappingConstant.LOGIN_PAGE_KEY);
                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                commandResult = new CommandResult(page, NavigationType.REDIRECT);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }

    private Cart makeNewUserCart(UserWithoutPassword user) {
        return Cart.builder()
                .userId(user.getUserId())
                .build();
    }
}
