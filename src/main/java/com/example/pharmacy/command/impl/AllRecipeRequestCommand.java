package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.entity.RecipeRequest;
import com.example.pharmacy.entity.UserWithoutPassword;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.service.RecipeService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("Duplicates")
public class AllRecipeRequestCommand implements Command {
    private static final String SESSION_USER = "user";
    private static final String ALL_REQUSET_MAP = "allRequestMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ALL_RECIPE_REQUEST_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);
        UserWithoutPassword user = (UserWithoutPassword) request.getSession().getAttribute(SESSION_USER);
        Map<RecipeRequest, Map.Entry<Drug, DrugDosage>> allRequestMap = new HashMap<>();
        try {
            List<RecipeRequest> requestList = RecipeService.getInstance().findAllRecipeRequestByUserId(user.getUserId());
            if (!requestList.isEmpty()) {
                for (RecipeRequest recipeRequest : requestList) {
                    Optional<DrugDosage> optionalDrugDosage = DrugService.getInstance()
                            .findDrugDosageById(recipeRequest.getDrugDosageId());
                    if (optionalDrugDosage.isPresent()) {
                        DrugDosage drugDosage = optionalDrugDosage.get();
                        Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugDosage.getDrugId());
                        if (optionalDrug.isPresent()) {
                            Drug drug = optionalDrug.get();
                            Map.Entry<Drug, DrugDosage> drugAndDosage = new AbstractMap.SimpleEntry<>(drug, drugDosage);
                            allRequestMap.put(recipeRequest, drugAndDosage);
                        }
                    }
                }
                request.setAttribute(ALL_REQUSET_MAP, allRequestMap);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
