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
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.service.OrderService;
import com.example.pharmacy.service.RecipeService;
import com.example.pharmacy.validator.OrderValidator;
import com.example.pharmacy.validator.UserPaymentDataValidator;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class DoOrderCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "orderSuccessMessage";
    private static final String IF_ERROR_MESSAGE = "orderErrorMessage";
    private static final String SESSION_CART = "cart";
    private static final String SESSION_USER_PAYMENT_DATA = "userPaymentData";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        CommandResult commandResult = null;
        String page;

        Cart cart = (Cart) request.getSession().getAttribute(SESSION_CART);
        DrugDosage drugDosage = cart.getDrugDosage();
        Long userId = cart.getUserId();
        BigDecimal totalPrice = cart.getTotalPrice();
        Boolean recipeNedeed = cart.getRecipe();
        Integer cartQuantity = cart.getQuantity();
        UserPaymentData userPaymentData = (UserPaymentData) request.getSession().getAttribute(SESSION_USER_PAYMENT_DATA);

        try {
            if (cartQuantity > 0 &&
                    OrderValidator.isEnoughDrug(cartQuantity, drugDosage.getDrugId()) &&
                    UserPaymentDataValidator.isEnoughBalance(totalPrice, userPaymentData)) {
                if (recipeNedeed) {
                    Optional<Recipe> optionalRecipe = RecipeService.getInstance().findRecipeByUserIdAndDosageId(userId, drugDosage.getDosageId());
                    if (optionalRecipe.isPresent()) {
                        Recipe recipe = optionalRecipe.get();
                        if (recipe.getExpirationDate().isAfter(LocalDate.now())) {
                            Order order = OrderService.getInstance()
                                    .makeOrder(userId, drugDosage.getDosageId(), LocalDateTime.now(), cartQuantity,
                                            totalPrice, OrderOrRecipeStatus.WAITING);
                            if (OrderService.getInstance().saveOrder(order) &&
                                    DrugService.getInstance().subtractDrugStorageQuantity(cartQuantity, drugDosage.getDrugId())) {
                                UserPaymentData updatedPaymentData = OrderService.getInstance().payOrder(totalPrice, userId);
                                request.getSession().setAttribute(SESSION_CART, getCleanCart(userId));
                                request.getSession().setAttribute(SESSION_USER_PAYMENT_DATA, updatedPaymentData);
                                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                                page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.USER_MAIN_PAGE_KEY);
                                commandResult = new CommandResult(page, NavigationType.REDIRECT);
                            }
                        } else {
                            request.setAttribute(IF_ERROR_MESSAGE, FLAG);
                            page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.CART_PAGE_KEY);
                            commandResult = new CommandResult(page, NavigationType.FORWARD);
                        }
                    } else {
                        request.setAttribute(IF_ERROR_MESSAGE, FLAG);
                        page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.CART_PAGE_KEY);
                        commandResult = new CommandResult(page, NavigationType.FORWARD);
                    }
                } else {
                    Order order = OrderService.getInstance()
                            .makeOrder(userId, drugDosage.getDosageId(), LocalDateTime.now(), cartQuantity,
                                    totalPrice, OrderOrRecipeStatus.WAITING);
                    if (OrderService.getInstance().saveOrder(order) &&
                            DrugService.getInstance().subtractDrugStorageQuantity(cartQuantity, drugDosage.getDrugId())) {
                        UserPaymentData updatedPaymentData = OrderService.getInstance().payOrder(totalPrice, userId);
                        request.getSession().setAttribute(SESSION_CART, getCleanCart(userId));
                        request.getSession().setAttribute(SESSION_USER_PAYMENT_DATA, updatedPaymentData);
                        request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                        page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.USER_MAIN_PAGE_KEY);
                        commandResult = new CommandResult(page, NavigationType.REDIRECT);
                    } else {
                        request.setAttribute(IF_ERROR_MESSAGE, FLAG);
                        page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.CART_PAGE_KEY);
                        commandResult = new CommandResult(page, NavigationType.FORWARD);
                    }
                }
            } else {
                request.setAttribute(IF_ERROR_MESSAGE, FLAG);
                page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.CART_PAGE_KEY);
                commandResult = new CommandResult(page, NavigationType.FORWARD);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }

    private Cart getCleanCart(Long userId) {
        return Cart.builder().userId(userId).build();
    }
}
