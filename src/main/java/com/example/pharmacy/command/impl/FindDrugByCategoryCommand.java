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
import java.util.Map;

public class FindDrugByCategoryCommand implements Command {
    private static final String CATEGORY_ID = "categoryId";
    private static final String CATEGORY_DRUG_LIST = "categoryDrugList";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = PageManager.getPageURI(PageMappingConstant.ALL_ABOUT_DRUG_PAGE_KEY);
        Long categoryId = Long.valueOf(request.getParameter(CATEGORY_ID));

        try {
            List<Map.Entry<Drug, List<DrugDosage>>> drugs = DrugService.getInstance().findAllDrugsByCategoryId(categoryId);
            request.setAttribute(CATEGORY_DRUG_LIST, drugs);
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return new CommandResult(page, NavigationType.FORWARD);
    }
}
