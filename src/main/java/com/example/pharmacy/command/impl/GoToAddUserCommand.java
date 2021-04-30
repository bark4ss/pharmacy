package com.example.pharmacy.command.impl;

import com.example.pharmacy.command.Command;
import com.example.pharmacy.command.CommandResult;
import com.example.pharmacy.command.NavigationType;
import com.example.pharmacy.entity.UserRole;
import com.example.pharmacy.exception.CommandException;
import com.example.pharmacy.manager.PageManager;
import com.example.pharmacy.manager.PageMappingConstant;

import javax.servlet.http.HttpServletRequest;

public class GoToAddUserCommand implements Command {
    private static final String ROLES = "roles";

    @Override
    public CommandResult execute(HttpServletRequest request) throws CommandException {
        String page = request.getContextPath() + PageManager.getPageURI(PageMappingConstant.ADMIN_ADD_USER_PAGE_KEY);
        CommandResult commandResult = new CommandResult(page, NavigationType.FORWARD);
        request.setAttribute(ROLES, UserRole.values());
        return commandResult;
    }
}
