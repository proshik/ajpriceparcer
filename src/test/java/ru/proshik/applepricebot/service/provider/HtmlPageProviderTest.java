package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;

public class HtmlPageProviderTest {

    private HtmlPageProvider provider = new HtmlPageProvider();

    @Test
    public void provideSuccess(){
        HtmlPage page = provider.provide("https://aj.ru/");

        Assert.assertNotNull(page);
    }
}
