package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStorage implements Storage {
    protected Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    protected static final Comparator<Meal> MEAL_COMPARATOR = Comparator.comparing(Meal::getDate);

    @Override
    public void update(Meal meal, int id) {
        storage.replace(meal.getId(), meal);
    }

    @Override
    public void save(Meal meal) {
        storage.put(meal.getId(), meal);
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
