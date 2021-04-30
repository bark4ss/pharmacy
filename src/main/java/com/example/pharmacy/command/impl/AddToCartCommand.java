package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.Cart;
import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.entity.DrugDosage;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;

public class AddToCartCommand implements Command {
    private static final String SESSION_CART = "cart";
    private static final String DRUG_DOSAGE_ID = "drugDosageId";
    private static final String SELECTED_QUANTITY = "selectedQuantity";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = PageManager.getPageURI(PageMappingConstant.CART_PAGE_KEY);
        Cart cart = (Cart) request.getSession().getAttribute(SESSION_CART);
        Long drugDosageId = Long.valueOf(request.getParameter(DRUG_DOSAGE_ID));
        Integer selectedQuantity = Integer.valueOf(request.getParameter(SELECTED_QUANTITY));

        try {
            Optional<DrugDosage> optionalDrugDosage = DrugService.getInstance().findDrugDosageById(drugDosageId);
            if (optionalDrugDosage.isPresent()) {
                DrugDosage drugDosage = optionalDrugDosage.get();
                Optional<Drug> optionalDrug = DrugService.getInstance().findDrugById(drugDosage.getDrugId());
                if (optionalDrug.isPresent()) {
                    Drug drug = optionalDrug.get();
                    cart.setDrugDosage(drugDosage);
                    cart.setDrugName(drug.getName());
                    cart.setRecipe(drug.getRecipeNedeed());
                    cart.setQuantity(selectedQuantity);
                    cart.setTotalPrice(drug.getPrice().multiply(new BigDecimal(selectedQuantity)));
                }
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return new CommandResult(page, NavigationType.FORWARD);
    }
}
