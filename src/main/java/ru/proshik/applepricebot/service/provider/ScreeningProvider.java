package ru.proshik.applepricebot.service.provider;

import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;

import java.util.List;

public interface ScreeningProvider {

    List<Product> screening(Provider provider) throws ProviderParseException;

    List<Product> screening(Provider provider, ProductType productType) throws ProviderParseException;

    List<Product> screening(Provider provider, List<ProductType> productTypes) throws ProviderParseException;
}
