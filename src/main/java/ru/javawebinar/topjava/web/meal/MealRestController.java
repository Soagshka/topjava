package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public MealTo create(MealTo mealTo) {
        return MealsUtil.createTo(service.create(MealsUtil.create(mealTo)), mealTo.isExcess());
    }

    public MealTo get(Integer userId) {
        Meal meal = service.get(authUserId(), userId);
        return MealsUtil.createTo(meal, meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void delete(Integer userId) {
        service.delete(authUserId(), userId);
    }

    public void update(MealTo mealTo) {
        service.update(MealsUtil.create(mealTo), authUserId());
    }
}