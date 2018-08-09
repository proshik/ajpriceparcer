package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.service.ScreeningService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
public class AssortmentController {

    private final ScreeningService screeningService;

    @Autowired
    public AssortmentController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @PostMapping("assortment")
    public List<Assortment> assortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        return screeningService.provideProducts(store);
    }

}
