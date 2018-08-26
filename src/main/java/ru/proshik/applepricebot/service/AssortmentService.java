package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.model.AssortmentResp;
import ru.proshik.applepricebot.model.ProductResp;
import ru.proshik.applepricebot.repository.AssortmentRepository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssortmentService {

    private final AssortmentRepository assortmentRepository;

    @Autowired
    public AssortmentService(AssortmentRepository assortmentRepository) {
        this.assortmentRepository = assortmentRepository;
    }

    @Transactional
    public List<AssortmentResp> filterByParameters(LocalDate fetchDate,
                                                   FetchType fetchType,
                                                   String provider,
                                                   ProductType productType) {
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;
        if (fetchDate != null) {
            startOfDay = fetchDate.atStartOfDay();
            endOfDay = startOfDay.plusDays(1);
        } else {
            LocalDate previousDay = LocalDate.now().minusDays(1);
            startOfDay = previousDay.atStartOfDay();
            endOfDay = startOfDay.plusDays(1);
        }

        if (fetchType != null) {
            List<Assortment> assortment = assortmentRepository.findByFetchDateAndFetchType(startOfDay, endOfDay, fetchType);

            return transformAssortment(assortment);
        }

        return Collections.emptyList();
    }

    private List<AssortmentResp> transformAssortment(List<Assortment> assortment) {
        return assortment.stream()
                .map(a -> AssortmentResp.builder()
                        .provider(a.getProvider().getTitle())
                        .fetchType(a.getFetchType())
                        .fetchDate(a.getFetchDate())
                        .products(transform(a.getProducts()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductResp> transform(List<Product> products) {
        return products.stream()
                .map(p -> ProductResp.builder()
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .available(p.getAvailable())
                        .price(p.getPrice())
                        .productType(p.getProductType())
                        .build())
                .collect(Collectors.toList());
    }
}

