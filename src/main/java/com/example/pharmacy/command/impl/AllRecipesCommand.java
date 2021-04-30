package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.entity.Recipe;
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
public class AllRecipesCommand implements Command {
    private static final String SESSION_USER = "user";
    private static final String ALL_RECIPE_MAP = "allRecipeMap";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ALL_RECIPES_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);

        UserWithoutPassword user = (UserWithoutPassword) request.getSession().getAttribute(SESSION_USER);
        Map<Recipe, Map.Entry<Drug, DrugDosage>> allRecipeMap = new HashMap<>();
        try {
            List<Recipe> recipeList = RecipeService.getInstance().findAllRecipesByUserId(user.getUserId());
            if (!recipeList.isEmpty()) {
                for (Recipe recipe : recipeList) {
                    Optional<DrugDosage> optionalDrugDosage = DrugService.getInstance().findDrugDosageById(recipe.getDrugDosageId());
                    if (optionalDrugDosage.isPresent()) {
                        DrugDosage drugDosage = optionalDrugDosage.get();
                        Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugDosage.getDrugId());
                        if (optionalDrug.isPresent()) {
                            Drug drug = optionalDrug.get();
                            Map.Entry<Drug, DrugDosage> dosageAndDrug = new AbstractMap.SimpleEntry<>(drug, drugDosage);
                            allRecipeMap.put(recipe, dosageAndDrug);
                        }
                    }
                }
                request.setAttribute(ALL_RECIPE_MAP, allRecipeMap);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
