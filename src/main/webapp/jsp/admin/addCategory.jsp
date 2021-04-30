<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Main</title>
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/admin.css" rel="stylesheet">
</head>
<body>
<%@include file="/jsp/admin/adminHeader.jsp" %>
<main role="main" class="col-md-10 ml-sm-auto col-lg-10 px-4">
    <div class="px-3 py-3 pt-md-5 pb-md-4 mx-auto text-center">
        <form method="post" action="${pageContext.request.contextPath}/mainController">
            <input type="hidden" name="command" value="add_category">
            <div class="col-md-6 mb-3">
                <fmt:message var="inputTitle" key="input.login.title"/>
                <fmt:message var="categoryName" key="admin.add.category.form.label"/>
                <label style="font-size: large" for="categoryName">${categoryName}</label>
                <fmt:message var="categoryPlaceholder" key="admin.add.category.placeholder"/>
                <input type="text" class="form-control" id="categoryName" name="categoryName"
                       placeholder="${categoryPlaceholder}" required pattern="^[A-Z][a-z\s]{1,63}$|^[А-Я][а-яёъ\s]{1,63}$"
                       title="${inputTitle}" oninvalid="setCustomValidity(loc())" oninput="setCustomValidity('')"/></br>
                <fmt:message var="addCategory" key="admin.add.category.label"/>
                <button class="btn " type="submit">
                    <span data-feather="plus-circle"></span>
                    ${addCategory} <span class="sr-only">(current)</span>
                </button>
            </div>
        </form>
    </div>
</main>
<%@include file="/jsp/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery-3.3.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"
        integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut"
        crossorigin="anonymous"></script>
<!-- Icons -->
<script src="${pageContext.request.contextPath}/resources/js/feather.min.js"></script>
<script>
    feather.replace()
</script>
<script>
    function loc() {
        var lng = document.getElementById("language").value;
        if (lng === 'ru_RU'){
            return 'Неверный формат';
        } else {
            return 'Incorrect format';
        }
    }
</script>
</body>
</html>
