<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Описание приема пищи</title>
</head>
<body>
<section>
    <h2>Описание : </h2>
    <p> ${meal.description}</p>
    <h2>Калории : </h2>
    <p> ${meal.calories}</p>
    <h2>Дата-время приема пищи : </h2>
    <p> ${f:formatLocalDateTime(meal.dateTime)}</p>
    <p>
</section>
<h3><a href="meals?id=${meal.id}&action=edit">Изменить</a></h3>
<h3><a href="index.html">Home</a></h3>
<h3><a href="meals">Meals</a></h3>
</body>
</html>