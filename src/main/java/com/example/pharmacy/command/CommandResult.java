package com.example.pharmacy.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommandResult {
    private String page;
    private NavigationType navigationType;

}
