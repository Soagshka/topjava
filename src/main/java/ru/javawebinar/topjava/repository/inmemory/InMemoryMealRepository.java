package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        Meal mealFromRepoById = repository.get(meal.getId());
        if (mealFromRepoById != null
                && userIdCheck(mealFromRepoById.getId(), userId)) {
            meal.setUserId(userId);
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        }
        throw new NotFoundException("No such meal with id = " + meal.getId());
    }

    @Override
    public boolean delete(int id, Integer userId) {
        log.info("delete {} for userId {}", id, userId);
        if (userIdCheck(id, userId)) {
            return repository.remove(id) != null;
        }
        return false;
    }

    @Override
    public Meal get(int id, Integer userId) {
        log.info("get {} for userId {}", id, userId);
        if (userIdCheck(id, userId)) {
            return repository.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        log.info("getAll");
        return repository
                .values()
                .stream()
                .filter(meal -> userId.equals(meal.getUserId()))
                .sorted(Comparator.comparing(Meal::getDate).reversed().thenComparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllByDate(Integer userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAllByDate");
        return repository
                .values()
                .stream()
                .filter(meal -> meal.getDate().isAfter(startDate))
                .filter(meal -> meal.getDate().isBefore(endDate))
                .filter(meal -> userId.equals(meal.getUserId()))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean userIdCheck(int id, Integer userId) {
        return userId.equals(repository.get(id).getUserId());
    }
}

