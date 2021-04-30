package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugCategory;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class AllDrugsCommand implements Command {
    private static final String ALL_DRUGS_MAP = "allDrugsMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = PageManager.getPageURI(PageMappingConstant.ALL_DRUGS_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);

        Map<DrugCategory, List<Map.Entry<Drug, List<DrugDosage>>>> allDrugsMap = new HashMap<>();
        try {
            List<DrugCategory> categories = DrugService.getInstance().findAllDrugCategory();
            for (DrugCategory category : categories) {
                List<Map.Entry<Drug, List<DrugDosage>>> categoryDrugs =
                        DrugService.getInstance().findAllDrugsByCategoryId(category.getCategoryId());
                allDrugsMap.put(category, categoryDrugs);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        request.setAttribute(ALL_DRUGS_MAP, allDrugsMap);
        return commandResult;
    }
}
