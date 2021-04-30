package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.entity.Order;
import com.example.pharmacy.entity.OrderOrRecipeStatus;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class ChangeOrderStatusCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "orderSuccess";
    private static final String IF_ERROR_MESSAGE = "orderFailed";
    private static final String ORDER_ID = "orderId";
    private static final String CHANGED_STATUS = "changedStatus";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        Long orderId = Long.valueOf(request.getParameter(ORDER_ID));
        OrderOrRecipeStatus status = OrderOrRecipeStatus.valueOf(request.getParameter(CHANGED_STATUS));
        switch (status) {
            case WAITING:
                changeOrderStatus(request, orderId, status);
                break;
            case DECLINED:
                try {
                    Optional<Order> optionalOrder = OrderService.getInstance().findOrderById(orderId);
                    if (optionalOrder.isPresent()) {
                        Order order = optionalOrder.get();
                        Optional<DrugDosage> optionalDrugDosage = DrugService.getInstance().findDrugDosageById(order.getDrugDosageId());
                        if (optionalDrugDosage.isPresent()) {
                            DrugDosage drugDosage = optionalDrugDosage.get();
                            if (DrugService.getInstance()
                                    .returnDrugToStorageQuantity(order.getQuantity(), drugDosage.getDrugId()) &&
                                    OrderService.getInstance().updateOrderStatus(orderId, status)) {
                                OrderService.getInstance().returnOrderCost(order.getOrderCost(), order.getUserId());
                                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                            } else {
                                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                            }
                        }
                    }
                } catch (ServiceException e) {
                    throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
                }
                break;
            case ACCEPTED:
                changeOrderStatus(request, orderId, status);
                break;
            default:
                throw new EnumConstantNotPresentException(OrderOrRecipeStatus.class, status.name());
        }
        return commandResult;
    }

    private void changeOrderStatus(HttpServletRequest request, Long orderId, OrderOrRecipeStatus status) throws CommandException {
        try {
            if (OrderService.getInstance().updateOrderStatus(orderId, status)) {
                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
            } else {
                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
    }
}
