<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>Meals</h2></caption>
        <tr>
            <th>Время</th>
            <th>Описание</th>
            <th>Калории</th>
        </tr>
        <jsp:useBean id="mealsTo" scope="request" type="java.util.List"/>
        <c:forEach var="meals" items="${mealsTo}">
            <jsp:useBean id="meals" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr style="background-color:${meals.excess ? 'greenyellow' : 'red'}">
                <td><c:out value="${meals.dateTime}"/></td>
                <td><c:out value="${meals.description}"/></td>
                <td><c:out value="${meals.calories}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>