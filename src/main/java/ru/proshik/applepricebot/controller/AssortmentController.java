package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.model.AssortmentResp;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.AssortmentService;
import ru.proshik.applepricebot.service.ScreeningService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/assortment")
public class AssortmentController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    private final ScreeningService screeningService;

    private final AssortmentService assortmentService;

    @Autowired
    public AssortmentController(ScreeningService screeningService, AssortmentService assortmentService) {
        this.screeningService = screeningService;
        this.assortmentService = assortmentService;
    }

    @PostMapping
    public List<Assortment> provideAssortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        return screeningService.provideProducts(FetchType.BY_REQUEST, store);
    }

    @GetMapping("filter")
    public List<AssortmentResp> filter(@RequestParam(value = "fetchDate", required = false) String fetchDateIn,
                                       @RequestParam(value = "fetchType", required = false) FetchType fetchType,
                                       @RequestParam(value = "providerType", required = false) ProviderType providerType,
                                       @RequestParam(value = "productType", required = false) ProductType productType) {
        LocalDate fetchDate = parseFetchDate(fetchDateIn);

        return assortmentService.filterByParameters(fetchDate, fetchType, providerType, productType);
    }

    private LocalDate parseFetchDate(String fetchDateIn) {
        if (fetchDateIn != null) {
            return LocalDate.parse(fetchDateIn, DATE_FORMATTER);
        }

        return null;
    }

}
