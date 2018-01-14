package ru.proshik.applepriceparcer.provider;

import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;

public interface Provider {

    Shop getShop();

    Assortment screening() throws ProviderParseException;

}
