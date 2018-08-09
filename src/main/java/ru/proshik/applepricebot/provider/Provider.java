package ru.proshik.applepricebot.provider;

import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.model.ProviderInfo;
import ru.proshik.applepricebot.repository.model.Product;

import java.util.List;

public interface Provider {

    List<Product> screening(ProviderInfo providerInfo) throws ProviderParseException;
}
