package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, mealMap -> new HashMap<>()).put(meal.getId(), meal);
            return meal;
        }
        if (get(meal.getId(), userId) != null) {
            Map<Integer, Meal> integerMealMap = repository.computeIfPresent(userId, (id, oldMeal) -> {
                oldMeal.computeIfPresent(meal.getId(), (integer, oldInnerMeal) -> meal);
                return oldMeal;
            });
            return integerMealMap != null ? integerMealMap.get(meal.getId()) : null;
        }
        return null;
    }

    @Override
    public boolean delete(int id, Integer userId) {
        log.info("delete {} for userId {}", id, userId);
        return (get(id, userId) != null) && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, Integer userId) {
        log.info("get {} for userId {}", id, userId);
        Map<Integer, Meal> mealMap = repository.get(userId);
        return (mealMap != null) ? mealMap.get(id) : null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        log.info("getAll");
        return repository.get(userId)
                .values()
                .stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllByDateTime(Integer userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAllByDateTime");
        return repository.get(userId)
                .values()
                .stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpenDates(meal.getDate(), startDate, endDate))
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

