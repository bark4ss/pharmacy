package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class GoToChangeOrderCommand implements Command {
    private static final String ORDER_ID = "orderId";
    private static final String OLD_ORDER_ID = "oldOrderId";
    private static final String DRUG_ID = "drugId";
    private static final String DRUG = "drug";
    private static final String DOSAGES = "dosages";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page;
        CommandResult commandResult = null;
        Long orderId = Long.valueOf(request.getParameter(ORDER_ID));
        Long drugId = Long.valueOf(request.getParameter(DRUG_ID));
        try {
            Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugId);
            if (optionalDrug.isPresent()) {
                Drug drug = optionalDrug.get();
                List<DrugDosage> drugDosages = DrugService.getInstance().findAllDrugDosageByDrugId(drug.getDrugId());
                request.setAttribute(OLD_ORDER_ID, orderId);
                request.setAttribute(DRUG, drug);
                request.setAttribute(DOSAGES, drugDosages);
                page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.CHANGE_ORDER_PAGE_KEY);
                commandResult = new CommandResult(page, NavigationType.FORWARD);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
