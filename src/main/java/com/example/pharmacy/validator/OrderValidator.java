package com.example.pharmacy.validator;

import com.example.pharmacy.entity.Drug;
import com.example.pharmacy.exception.ServiceException;
import com.example.pharmacy.service.DrugService;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class OrderValidator {
    /**
     * Check in database is enough drug for order
     *
     * @param currentQuantity its a quantity in user's order
     * @param drugId          its a drug id which quantity need to check
     * @return {@code true} if success or {@code false} if failed
     * @throws ServiceException if Dao layer can't do own method
     */
    public boolean isEnoughDrug(Integer currentQuantity, Long drugId) throws ServiceException {
        Optional<Drug> drugById = DrugService.getInstance().findDrugById(drugId);
        return drugById.filter(drug -> currentQuantity <= drug.getStorageQuantity()).isPresent();
    }
}
