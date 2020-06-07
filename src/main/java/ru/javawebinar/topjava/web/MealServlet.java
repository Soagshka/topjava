package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.SimpleStorage;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final AtomicInteger nextId = new AtomicInteger();

    private Storage storage;

    private List<Meal> meals = Arrays.asList(
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(nextId.getAndIncrement(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public void init(ServletConfig config) {
        storage = new SimpleStorage();
        for (Meal meal : meals) {
            storage.save(meal);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("mealsTo", MealsUtil.convertToMealToByStreams(storage.getAllSorted(), 2000));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        Meal meal = null;
        String identifier = request.getParameter("id");
        Integer id = null;
        if (identifier != null && !identifier.trim().isEmpty()) {
            id = Integer.parseInt(identifier);
        }
        switch (action) {
            case "delete":
                if (id != null) {
                    storage.delete(id);
                }
                response.sendRedirect("meals");
                return;
            case "view":
            case "edit":
                if (id != null) {
                    meal = storage.get(id);
                }
                break;
            case "add":
                meal = new Meal();
                break;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
        request.setAttribute("meal", meal);
        request.getRequestDispatcher(
                ("view".equals(action) ? "/view.jsp" : "/edit.jsp")
        ).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String identifier = request.getParameter("id");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        String date = request.getParameter("date");
        if (description.trim().isEmpty()) {
            response.sendRedirect("meals");
            return;
        }
        boolean isAlreadyExist = (identifier != null && !identifier.trim().isEmpty());
        Meal meal;
        if (isAlreadyExist) {
            meal = storage.get(Integer.parseInt(identifier));
            meal.setDescription(description);
            meal.setCalories(Integer.parseInt(calories));
            meal.setDateTime(LocalDateTime.parse(date));
            storage.update(meal, meal.getId());
        } else {
            meal = new Meal(nextId.getAndIncrement(), LocalDateTime.parse(date), description, Integer.parseInt(calories));
            storage.save(meal);
        }

        response.sendRedirect("meals");
    }
}
