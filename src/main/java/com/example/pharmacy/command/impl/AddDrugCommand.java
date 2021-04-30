package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.validator.DrugValidator;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@SuppressWarnings("Duplicates")
public class AddDrugCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "addDrugSuccess";
    private static final String IF_ERROR_MESSAGE = "addDrugFailed";
    private static final String DRUG_NAME = "drugName";
    private static final String DRUG_DOSAGE_NAME = "drugDosageName";
    private static final String DRUG_COMPOSITION = "drugComposition";
    private static final String DRUG_INDICATIONS = "drugIndications";
    private static final String DRUG_APPLICATION = "drugApplication";
    private static final String DRUG_CONTRAINDICATIONS = "drugContra";
    private static final String DRUG_RECIPE = "recipe";
    private static final String DRUG_CATEGORY_ID = "drugCategoryId";
    private static final String DRUG_QUANTITY = "selectedQuantity";
    private static final String DRUG_PRICE = "drugPrice";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);

        String drugName = request.getParameter(DRUG_NAME);
        String drugDosageName = request.getParameter(DRUG_DOSAGE_NAME);
        String drugComposition = request.getParameter(DRUG_COMPOSITION);
        String drugIndications = request.getParameter(DRUG_INDICATIONS);
        String drugApplication = request.getParameter(DRUG_APPLICATION);
        String drugContra = request.getParameter(DRUG_CONTRAINDICATIONS);
        Boolean recipe = Boolean.valueOf(request.getParameter(DRUG_RECIPE));
        Long drugCategoryId = Long.valueOf(request.getParameter(DRUG_CATEGORY_ID));
        Integer selectedQuantity = Integer.valueOf(request.getParameter(DRUG_QUANTITY));
        BigDecimal drugPrice = new BigDecimal(request.getParameter(DRUG_PRICE));

        if (DrugValidator.isValidDrugName(drugName) &&
                DrugValidator.isValidComposition(drugComposition) &&
                DrugValidator.isValidIndications(drugIndications) &&
                DrugValidator.isValidModeOfApplication(drugApplication) &&
                DrugValidator.isValidContraindications(drugContra) && DrugValidator.isValidDrugDosageName(drugDosageName)) {

            Drug drug = DrugService.getInstance()
                    .makeDrug(drugName, drugComposition, drugIndications, drugApplication,
                            drugContra, recipe, drugCategoryId, selectedQuantity, drugPrice);
            try {
                Long drugId = DrugService.getInstance().addDrug(drug);
                if (DrugService.getInstance().addDrugDosage(drugId, drugDosageName)) {
                    request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                } else {
                    request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                }
            } catch (ServiceException e) {
                throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
            }
        } else {
            request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
        }
        return commandResult;
    }
}
