package ru.proshik.applepricebot.provider;

import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.storage.model.Fetch;

public interface Provider {

    Fetch screening() throws ProviderParseException;
}
