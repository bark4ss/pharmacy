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

public class GoToRecipeRequestCommand implements Command {
    private static final String DRUGS_MAP = "drugsMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page;
        CommandResult commandResult;
        try {
            List<Map.Entry<Drug, List<DrugDosage>>> drugs = DrugService.getInstance().findAllRecipeNedeedDrugs();
            request.setAttribute(DRUGS_MAP, drugs);
            page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.RECIPE_REQUEST_FORM_PAGE_KEY);
            commandResult = new CommandResult(page, NavigationType.FORWARD);
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
