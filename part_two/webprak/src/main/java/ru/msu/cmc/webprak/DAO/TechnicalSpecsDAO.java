package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.TechnicalSpecs;
import ru.msu.cmc.webprak.models.Cars;
import java.math.BigDecimal;
import java.util.Collection;

public interface TechnicalSpecsDAO extends CommonDAO<TechnicalSpecs, Long> {
    /**
     * Находит технические характеристики по автомобилю
     * @param car автомобиль
     * @return технические характеристики для указанного автомобиля или null, если не найдены
     */
    TechnicalSpecs findByCar(Cars car);

    /**
     * Находит автомобили с указанным типом топлива
     * @param fuelType тип топлива
     * @return коллекция технических характеристик автомобилей с указанным типом топлива
     */
    Collection<TechnicalSpecs> findByFuelType(TechnicalSpecs.FuelType fuelType);

    /**
     * Находит автомобили с мощностью двигателя в указанном диапазоне
     * @param minPower минимальная мощность
     * @param maxPower максимальная мощность
     * @return коллекция технических характеристик подходящих автомобилей
     */
    Collection<TechnicalSpecs> findByPowerBetween(int minPower, int maxPower);

    /**
     * Находит автомобили с автоматической коробкой передач
     * @param automatic признак автоматической КПП
     * @return коллекция технических характеристик автомобилей с указанным типом КПП
     */
    Collection<TechnicalSpecs> findByAutomaticTransmission(boolean automatic);

    /**
     * Находит автомобили с объемом двигателя в указанном диапазоне
     * @param minVolume минимальный объем двигателя
     * @param maxVolume максимальный объем двигателя
     * @return коллекция технических характеристик подходящих автомобилей
     */
    Collection<TechnicalSpecs> findByEngineVolumeBetween(BigDecimal minVolume, BigDecimal maxVolume);

    /**
     * Находит автомобили с указанным количеством дверей и сидений
     * @param doors количество дверей
     * @param seats количество сидений
     * @return коллекция технических характеристик подходящих автомобилей
     */
    Collection<TechnicalSpecs> findByDoorsAndSeats(int doors, int seats);

    /**
     * Находит автомобили по наличию/отсутствию круиз-контроля
     * @param hasCruiseControl признак наличия круиз-контроля
     * @return коллекция технических характеристик подходящих автомобилей
     */
    Collection<TechnicalSpecs> findByCruiseControl(boolean hasCruiseControl);

    /**
     * Находит автомобили с расходом топлива менее указанного значения
     * @param maxConsumption максимальный расход топлива
     * @return коллекция технических характеристик подходящих автомобилей
     */
    Collection<TechnicalSpecs> findByFuelConsumptionLessThan(BigDecimal maxConsumption);
}