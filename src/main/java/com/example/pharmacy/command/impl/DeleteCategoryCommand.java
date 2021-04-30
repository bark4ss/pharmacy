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

@SuppressWarnings("Duplicates")
public class DeleteCategoryCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "categoryDeleteSuccess";
    private static final String IF_ERROR_MESSAGE = "categoryDeleteFailed";
    private static final String CATEGORY_ID = "categoryId";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        Long categoryId = Long.valueOf(request.getParameter(CATEGORY_ID));
        try {
            List<Map.Entry<Drug, List<DrugDosage>>> categoryDrugs = DrugService.getInstance().findAllDrugsByCategoryId(categoryId);
            for (Map.Entry<Drug, List<DrugDosage>> drugMap : categoryDrugs) {
                DrugService.getInstance().deleteDrug(drugMap.getKey().getDrugId());
            }
            if (DrugService.getInstance().deleteCategory(categoryId)) {
                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
            } else {
                request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
            }
        } catch (ServiceException e) {
            throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
        }
        return commandResult;
    }
}
