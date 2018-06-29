package ru.proshik.applepricebot.provider;

import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.storage.model.Fetch;

import java.util.List;

public interface Provider {

    List<Product> screening() throws ProviderParseException;
}
