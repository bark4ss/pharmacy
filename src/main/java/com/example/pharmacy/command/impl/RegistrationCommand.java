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
import com.example.pharmacy.validator.UserDataValidator;
import com.example.pharmacy.validator.UserPaymentDataValidator;
import com.example.pharmacy.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@SuppressWarnings("Duplicates")
public class RegistrationCommand implements Command {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String SESSION_CART = "cart";
    private static final String SESSION_USER = "user";
    private static final String SESSION_USER_DATA = "userData";
    private static final String SESSION_USER_PAYMENT_DATA = "userPaymentData";
    private static final boolean FLAG = true;
    private static final String IF_ERROR_MESSAGE = "registrationMessage";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        CommandResult commandResult;
        String page;
        String loginValue = request.getParameter(LOGIN);
        String passwordValue = request.getParameter(PASSWORD);
        String firstNameValue = request.getParameter(FIRST_NAME);
        String lastNameValue = request.getParameter(LAST_NAME);
        String emailValue = request.getParameter(EMAIL);
        String birthdayValue = request.getParameter(BIRTHDAY);
        String cardNumber = request.getParameter(CARD_NUMBER);
        UserRole role = UserRole.USER;

        try {
            if (UserValidator.isValidLogin(loginValue) &&
                    UserValidator.isValidPassword(passwordValue) &&
                    UserDataValidator.isValidFirstName(firstNameValue) &&
                    UserDataValidator.isValidLastName(lastNameValue) &&
                    UserDataValidator.isValidEmail(emailValue) &&
                    UserDataValidator.isValidBirthday(birthdayValue) &&
                    UserPaymentDataValidator.isValidCardNumber(cardNumber) &&
                    !UserService.getInstance().isUsernameExist(loginValue) &&
                    !UserService.getInstance().isEmailExist(emailValue)) {

                Long cardNumberValue = Long.valueOf(cardNumber);
                User user = UserService.getInstance().makeUserFromData(loginValue, PasswordEncoder.encodePassword(passwordValue), role);
                UserData userData = UserService.getInstance()
                        .makeUserData(firstNameValue, lastNameValue, emailValue, LocalDate.parse(birthdayValue));

                UserPaymentData userPaymentData = UserService.getInstance().makePaymentData(cardNumberValue);

                UserWithoutPassword userWithoutPassword = UserService.getInstance().saveUser(user, userData, userPaymentData);
                page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.USER_MAIN_PAGE_KEY);
                Cart cart = buildNewCart(userWithoutPassword);
                request.getSession().setAttribute(SESSION_CART, cart);
                request.getSession().setAttribute(SESSION_USER, userWithoutPassword);
                request.getSession().setAttribute(SESSION_USER_DATA, userData);
                request.getSession().setAttribute(SESSION_USER_PAYMENT_DATA, userPaymentData);
                commandResult = new CommandResult(page, NavigationType.REDIRECT);
            } else {
                page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.REGISTRATION_PAGE_KEY);
                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                commandResult = new CommandResult(page, NavigationType.REDIRECT);

            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }

    private Cart buildNewCart(UserWithoutPassword userWithoutPassword) {
        return Cart.builder()
                .userId(userWithoutPassword.getUserId())
                .build();
    }
}
