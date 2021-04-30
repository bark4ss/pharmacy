package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.entity.Order;
import com.example.pharmacy.entity.UserWithoutPassword;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("Duplicates")
public class AllOrdersCommand implements Command {
    private static final String SESSION_USER = "user";
    private static final String ALL_ORDER_MAP = "allOrderMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ALL_ORDERS_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);
        UserWithoutPassword user = (UserWithoutPassword) request.getSession().getAttribute(SESSION_USER);
        Map<Order, Map.Entry<Drug, DrugDosage>> allOrderMap = new HashMap<>();

        try {
            List<Order> orderList = OrderService.getInstance().getOrderListByUserId(user.getUserId());
            if (!orderList.isEmpty()) {
                for (Order order : orderList) {
                    Optional<DrugDosage> optionalDrugDosage = DrugService.getInstance().findDrugDosageById(order.getDrugDosageId());
                    if (optionalDrugDosage.isPresent()) {
                        DrugDosage drugDosage = optionalDrugDosage.get();
                        Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugDosage.getDrugId());
                        if (optionalDrug.isPresent()) {
                            Drug drug = optionalDrug.get();
                            Map.Entry<Drug, DrugDosage> dosageAndDrug = new AbstractMap.SimpleEntry<>(drug, drugDosage);
                            allOrderMap.put(order, dosageAndDrug);
                        }
                    }
                }
                request.setAttribute(ALL_ORDER_MAP, allOrderMap);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
