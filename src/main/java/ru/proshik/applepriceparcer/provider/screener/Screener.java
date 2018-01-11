package ru.proshik.applepriceparcer.provider.screener;

import ru.proshik.applepriceparcer.provider.model.Assortment;
import ru.proshik.applepriceparcer.provider.model.Shop;

public interface Screener {

    Shop supplier();

    Assortment screening();

}
