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
import com.example.pharmacy.service.RecipeService;
import com.example.pharmacy.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("Duplicates")
public class ShowRecipeRequestCommand implements Command {
    private static final String ALL_STATUS = "allStatus";
    private static final String REQUEST_MAP = "requestMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.DOCTOR_ALL_RECIPE_REQUEST_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);
        Map<Map.Entry<UserData, RecipeRequest>, Map.Entry<Drug, DrugDosage>> allUserRequestMap = new HashMap<>();

        try {
            List<RecipeRequest> requestList = RecipeService.getInstance().findAllRecipeRequest();
            if (!requestList.isEmpty()) {
                for (RecipeRequest recipeRequest : requestList) {
                    Optional<UserWithoutPassword> optionalUser = UserService.getInstance()
                            .findUserWithoutPasswordById(recipeRequest.getUserId());
                    if (optionalUser.isPresent()) {
                        UserWithoutPassword user = optionalUser.get();
                        Optional<UserData> optionalUserData = UserService.getInstance().findUserDataByUserId(user.getUserId());
                        if (optionalUserData.isPresent()) {
                            UserData userData = optionalUserData.get();
                            Map.Entry<UserData, RecipeRequest> userDataAndRequest = new AbstractMap.SimpleEntry<>(userData, recipeRequest);
                            Optional<DrugDosage> optionalDosage = DrugService.getInstance().findDrugDosageById(recipeRequest.getDrugDosageId());
                            if (optionalDosage.isPresent()) {
                                DrugDosage drugDosage = optionalDosage.get();
                                Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugDosage.getDrugId());
                                if (optionalDrug.isPresent()) {
                                    Drug drug = optionalDrug.get();
                                    Map.Entry<Drug, DrugDosage> drugAndDosage = new AbstractMap.SimpleEntry<>(drug, drugDosage);
                                    allUserRequestMap.put(userDataAndRequest, drugAndDosage);
                                }
                            }
                        }
                    }
                }
                request.setAttribute(ALL_STATUS, OrderOrRecipeStatus.values());
                request.setAttribute(REQUEST_MAP, allUserRequestMap);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
