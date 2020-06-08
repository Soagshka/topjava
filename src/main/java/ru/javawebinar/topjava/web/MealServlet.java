package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.InMemoryMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    private MealStorage mealStorage;

    public void init(ServletConfig config) {
        mealStorage = new InMemoryMealStorage();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug(request.toString());
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("mealsTo", MealsUtil.convertToMealToByStreams(mealStorage.getAllSorted(), DEFAULT_CALORIES_PER_DAY));
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
                    mealStorage.delete(id);
                }
                response.sendRedirect("meals");
                return;
            case "view":
            case "edit":
                if (id != null) {
                    meal = mealStorage.get(id);
                }
                break;
            case "add":
                meal = new Meal();
                break;
            default:
                response.sendRedirect("meals");
                return;
        }
        request.setAttribute("meal", meal);
        request.getRequestDispatcher(
                ("view".equals(action) ? "/view.jsp" : "/edit.jsp")
        ).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug(request.toString());
        request.setCharacterEncoding("UTF-8");
        String identifier = request.getParameter("id");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        String date = request.getParameter("date");
        if (identifier != null && !identifier.trim().isEmpty()) {
            mealStorage.update(new Meal(LocalDateTime.parse(date), description, Integer.parseInt(calories)), Integer.parseInt(identifier));
        } else {
            mealStorage.create(new Meal(LocalDateTime.parse(date), description, Integer.parseInt(calories)));
        }
        String caloriesPerDay = request.getParameter("caloriesPerDay");
        if (!caloriesPerDay.isEmpty()) {
            request.setAttribute("mealsTo", MealsUtil.convertToMealToByStreams(mealStorage.getAllSorted(), Integer.parseInt(caloriesPerDay)));
        }

        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
