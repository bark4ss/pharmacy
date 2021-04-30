package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.OrderOrRecipeStatus;
import com.example.pharmacy.entity.RecipeRequest;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.exception.ExceptionMessage;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;
import com.example.pharmacy.service.RecipeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class ChangeRequestCommand implements Command {
    private static final boolean FLAG = true;
    private static final String IF_SUCCESS_MESSAGE = "requestSuccess";
    private static final String IF_ERROR_MESSAGE = "requestError";
    private static final String REQUEST_ID = "requestId";
    private static final String CHANGED_STATUS = "changedStatus";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.DOCTOR_MAIN_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.REDIRECT);
        Long requestId = Long.valueOf(request.getParameter(REQUEST_ID));
        OrderOrRecipeStatus status = OrderOrRecipeStatus.valueOf(request.getParameter(CHANGED_STATUS));
        switch (status) {
            case WAITING:
                request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                break;
            case DECLINED:
                try {
                    if (RecipeService.getInstance().declineRecipeRequest(requestId)) {
                        request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                    } else {
                        request.getSession().setAttribute(IF_ERROR_MESSAGE, FLAG);
                    }
                    break;
                } catch (ServiceException e) {
                    throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
                }
            case ACCEPTED:
                try {
                    Optional<RecipeRequest> optionalRecipeRequest = RecipeService.getInstance()
                            .findRecipeRequestById(requestId);
                    if (optionalRecipeRequest.isPresent()) {
                        RecipeRequest recipeRequest = optionalRecipeRequest.get();
                        if (RecipeService.getInstance().acceptRecipeRequest(recipeRequest)) {
                            request.getSession().setAttribute(IF_SUCCESS_MESSAGE, FLAG);
                        }
                    }
                    break;
                } catch (ServiceException e) {
                    throw new CommandException(ExceptionMessage.COMMAND_EXCEPTION_MESSAGE, e);
                }
            default:
                throw new EnumConstantNotPresentException(OrderOrRecipeStatus.class, status.name());
        }
        return commandResult;
    }
}
