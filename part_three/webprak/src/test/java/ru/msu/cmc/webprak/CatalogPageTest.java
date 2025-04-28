package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogPageTest {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CATALOG_URL = BASE_URL + "/catalog";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testCatalogPageLoad() {
        driver.get(CATALOG_URL);

        // Проверка заголовка страницы
        assertEquals("Каталог авто | Автосалон", driver.getTitle());

        // Проверка основного заголовка
        WebElement pageHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h1[contains(text(), 'Каталог автомобилей')]")
                )
        );
        assertTrue(pageHeader.isDisplayed());
    }

    @Test
    void testBasicFilters() {
        driver.get(CATALOG_URL);

        // Проверка наличия основных фильтров
        WebElement brandFilter = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//select[@name='brand']")
                )
        );
        WebElement minPrice = driver.findElement(By.xpath("//input[@name='minPrice']"));
        WebElement maxPrice = driver.findElement(By.xpath("//input[@name='maxPrice']"));
        WebElement statusFilter = driver.findElement(By.xpath("//select[@name='status']"));
        WebElement applyButton = driver.findElement(By.xpath("//button[@type='submit']"));

        assertAll(
                () -> assertTrue(brandFilter.isDisplayed()),
                () -> assertTrue(minPrice.isDisplayed()),
                () -> assertTrue(maxPrice.isDisplayed()),
                () -> assertTrue(statusFilter.isDisplayed()),
                () -> assertTrue(applyButton.isDisplayed())
        );

        // Проверка работы фильтра по бренду
        Select brandSelect = new Select(brandFilter);
        brandSelect.selectByIndex(1); // Выбираем первый бренд из списка
        applyButton.click();

        wait.until(ExpectedConditions.urlContains("brand="));
        assertTrue(driver.getCurrentUrl().contains("brand="));
    }

    @Test
    void testAdvancedFilters() {
        driver.get(CATALOG_URL);

        // Проверка кнопки доп. фильтров
        WebElement advFilterButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(), 'Доп. фильтры')]")
                )
        );

        // Проверка, что доп. фильтры скрыты
        WebElement advFiltersSection = driver.findElement(By.id("advancedFilters"));
        assertEquals("none", advFiltersSection.getCssValue("display"),
                "Доп. фильтры должны быть скрыты по умолчанию");

        // Двойной клик для открытия (если требуется)
        new Actions(driver)
                .click(advFilterButton)
                .click(advFilterButton)
                .perform();

        // Ждем открытия с явным ожиданием
        wait.until(d -> {
            String display = advFiltersSection.getCssValue("display");
            return display != null && !display.equals("none");
        });

        // Проверка доп. фильтров
        WebElement acFilter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//select[@name='hasAC']")
                )
        );
        WebElement fuelFilter = driver.findElement(By.xpath("//select[@name='fuelType']"));

        assertAll(
                () -> assertTrue(acFilter.isDisplayed(), "Фильтр кондиционера не отображается"),
                () -> assertTrue(fuelFilter.isDisplayed(), "Фильтр топлива не отображается")
        );
    }

    @Test
    void testSorting() {
        driver.get(CATALOG_URL);

        // Проверка кнопок сортировки
        WebElement priceDescBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href, 'sort=priceDesc')]")
                )
        );
        WebElement priceAscBtn = driver.findElement(
                By.xpath("//a[contains(@href, 'sort=priceAsc')]")
        );

        // Определяем изначально активную кнопку (если есть)
        boolean isDescActiveInitially = priceDescBtn.getAttribute("class").contains("active");
        boolean isAscActiveInitially = priceAscBtn.getAttribute("class").contains("active");

        // Клик по неактивной кнопке
        if (!isDescActiveInitially) {
            priceDescBtn.click();
        } else {
            priceAscBtn.click();
        }

        // Ждем применения сортировки
        wait.until(ExpectedConditions.urlContains("sort="));

        // Проверяем активность после клика
        WebElement activeSortBtn = wait.until(d -> {
            WebElement desc = d.findElement(By.xpath("//a[contains(@href, 'sort=priceDesc')]"));
            WebElement asc = d.findElement(By.xpath("//a[contains(@href, 'sort=priceAsc')]"));
            return desc.getAttribute("class").contains("active") ? desc :
                    asc.getAttribute("class").contains("active") ? asc : null;
        });

        assertNotNull(activeSortBtn, "Ни одна кнопка сортировки не активна");
        assertTrue(activeSortBtn.getAttribute("href").contains("sort="),
                "Активная кнопка должна содержать параметр сортировки");
    }

    @Test
    void testCarCards() {
        driver.get(CATALOG_URL);

        // Ожидаем появления хотя бы одной карточки
        wait.until(d -> !d.findElements(By.className("car-card")).isEmpty());

        List<WebElement> carCards = driver.findElements(
                By.xpath("//div[contains(@class, 'car-card') and .//h5[@class='card-title']]")
        );
        assertFalse(carCards.isEmpty(), "Карточки автомобилей не найдены");

        // Проверка первой карточки
        WebElement firstCard = carCards.get(0);

        // Прокрутка к элементу
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                firstCard
        );

        // Проверка изображения
        WebElement carImage = wait.until(
                ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                        firstCard, By.tagName("img")
                )
        ).get(0);
        assertAll(
                () -> assertTrue(carImage.isDisplayed()),
                () -> assertFalse(carImage.getAttribute("src").isBlank())
        );

        // Проверка заголовка
        WebElement title = firstCard.findElement(By.className("card-title"));
        assertFalse(title.getText().isBlank());

        // Проверка цены
        WebElement price = wait.until(
                d -> firstCard.findElement(By.xpath(".//p[contains(@class, 'text-success')]"))
        );
        assertTrue(price.getText().contains("₽"));

        // Проверка кнопки "Подробнее"
        WebElement detailsBtn = firstCard.findElement(
                By.xpath(".//a[contains(text(), 'Подробнее')]")
        );
        assertTrue(detailsBtn.isDisplayed());

        // Клик по кнопке
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", detailsBtn);
        wait.until(ExpectedConditions.urlContains("/cars/"));
    }

    @Test
    void testDiscountDisplay() {
        driver.get(CATALOG_URL);

        // Ищем карточки с акциями
        List<WebElement> discountedCards = driver.findElements(
                By.xpath("//div[contains(@class, 'car-card') and .//s[contains(@class, 'text-muted')]]")
        );

        if (!discountedCards.isEmpty()) {
            WebElement card = discountedCards.get(0);

            // Проверка старой цены
            WebElement oldPrice = card.findElement(By.tagName("s"));
            assertFalse(oldPrice.getText().isBlank());

            // Проверка новой цены
            WebElement newPrice = card.findElement(
                    By.xpath(".//p[contains(@class, 'text-success') and not(contains(@class, 'text-muted'))]")
            );
            assertFalse(newPrice.getText().isBlank());

            // Проверка бейджа скидки
            WebElement discountBadge = card.findElement(By.xpath(".//span[contains(@class, 'bg-danger')]"));
            assertTrue(discountBadge.getText().contains("%"));
        } else {
            System.out.println("Нет автомобилей со скидкой для тестирования");
        }
    }
}