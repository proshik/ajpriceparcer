package ru.proshik.applepricebot.repository;

import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomAssortmentRepository {

    List<Assortment> findAllWithNameOnly(LocalDateTime startOfDay,
                                         LocalDateTime endOfDay,
                                         FetchType fetchType,
                                         ProviderType providerType,
                                         ProductType productType);

}
