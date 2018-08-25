package ru.proshik.applepricebot.controller;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.model.AssortmentOut;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.service.AssortmentService;
import ru.proshik.applepricebot.service.ScreeningService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "api/v1/assortment")
public class AssortmentController {

    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    private final ScreeningService screeningService;

    private final AssortmentService assortmentService;

    @Autowired
    public AssortmentController(ScreeningService screeningService, AssortmentService assortmentService) {
        this.screeningService = screeningService;
        this.assortmentService = assortmentService;
    }

    @PostMapping
    public List<Assortment> assortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        return screeningService.provideProducts(store);
    }

    @GetMapping("filter")
    public List<AssortmentOut> filter(@RequestParam(value = "fetchDate", required = false) String fetchDateIn,
                                        @RequestParam(value = "fetchType", required = false) FetchType fetchType,
                                        @RequestParam(value = "provider", required = false) String provider,
                                        @RequestParam(value = "productType", required = false) ProductType productType) {

        LocalDate fetchDate = parseFetchDate(fetchDateIn);

        return assortmentService.filterByParameters(fetchDate, fetchType, provider, productType);
    }

    @Nullable
    private LocalDate parseFetchDate(String fetchDateIn) {
        if (fetchDateIn != null) {
            return LocalDate.parse(fetchDateIn, DATE_FORMATTER);
        }
        return null;
    }

}
