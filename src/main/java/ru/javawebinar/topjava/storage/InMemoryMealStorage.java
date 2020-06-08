package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealStorage implements MealStorage {
    private final Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private static final AtomicInteger nextId = new AtomicInteger();
    protected static final Comparator<Meal> MEAL_COMPARATOR = Comparator.comparing(Meal::getDate).thenComparing(Meal::getTime);

    public InMemoryMealStorage() {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        for (Meal meal : meals) {
            meal.setId(nextId.getAndIncrement());
            storage.put(meal.getId(), meal);
        }
    }

    @Override
    public Meal update(Meal meal, int id) {
        storage.replace(id, meal);
        return storage.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(nextId.getAndIncrement());
        storage.put(meal.getId(), meal);
        return storage.get(nextId.get());
    }

    @Override
    public Meal get(int id) {
        return storage.get(id);
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    @Override
    public List<Meal> getAllSorted() {
        ArrayList<Meal> meals = new ArrayList<>(storage.values());
        meals.sort(MEAL_COMPARATOR);
        return meals;
    }
}
