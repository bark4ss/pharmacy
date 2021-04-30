package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.DrugService;
import com.example.pharmacy.validator.DrugValidator;

import javax.servlet.http.HttpServletRequest;

public class AddCategoryCommand implements Command {
    private static final String CATEGORY_NAME = "categoryName";
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "addCategorySuccess";
    private static final String IF_ERROR_MESSAGE = "addCategoryFailed";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        String categoryName = request.getParameter(CATEGORY_NAME);
        try {
            if (DrugValidator.isValidCategoryName(categoryName) && DrugService.getInstance().addCategory(categoryName)) {
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
