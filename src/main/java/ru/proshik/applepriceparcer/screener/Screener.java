package ru.proshik.applepriceparcer.screener;

import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Shop;

public interface Screener {

    Shop getShop();

    Assortment screeningPage() throws ProviderParseException;

}
