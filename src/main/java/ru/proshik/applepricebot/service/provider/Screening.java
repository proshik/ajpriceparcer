package ru.proshik.applepricebot.service.provider;

import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.Provider;

import java.util.List;

public interface Screening {

    List<Product> screening(Provider provider) throws ProviderParseException;
}
