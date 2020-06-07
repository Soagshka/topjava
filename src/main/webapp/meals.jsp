<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>Meals</h2></caption>
        <tr>
            <th>Дата/Время</th>
            <th>Описание</th>
            <th>Калории</th>
        </tr>
        <jsp:useBean id="mealsTo" scope="request" type="java.util.List"/>
        <c:forEach var="meal" items="${mealsTo}">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr style="background-color:${meal.excess ? 'red' : 'greenyellow'}">
                <td>${f:formatLocalDateTime(meal.dateTime)}</td>
                <td><a href="meals?id=${meal.id}&action=view">${meal.description}</a></td>
                <td>${meal.calories}</td>
                <td><a href="meals?id=${meal.id}&action=delete"><img src="img/delete.png"></a></td>
                <td><a href="meals?id=${meal.id}&action=edit"><img src="img/pencil.png"></a></td>
            </tr>
        </c:forEach>
    </table>
    <br>
    <a href="meals?action=add">Добавить еду</a>
</div>
</body>
</html>