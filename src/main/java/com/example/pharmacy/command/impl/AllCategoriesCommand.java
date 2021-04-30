package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.DrugCategory;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AllCategoriesCommand implements Command {
    private static final String CATEGORY_LIST = "categoryList";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        List<DrugCategory> categoryList;
        String page = PageManager.getPageURI(PageMappingConstant.ALL_ABOUT_DRUG_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);
        try {
            categoryList = DrugService.getInstance().findAllDrugCategory();
            request.setAttribute(CATEGORY_LIST, categoryList);
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
