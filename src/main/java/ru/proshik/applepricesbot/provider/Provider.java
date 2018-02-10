package ru.proshik.applepricesbot.provider;

import ru.proshik.applepricesbot.exception.ProviderParseException;
import ru.proshik.applepricesbot.storage.model.Fetch;

public interface Provider {

    Fetch screening() throws ProviderParseException;
}
