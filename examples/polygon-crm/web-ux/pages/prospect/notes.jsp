<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Edit Notes</title>
</head>
<body>

<%--<div class="akki-styles" style="height:10px;">--%>
<%--    <div class="color uno"></div>--%>
<%--    <div class="color dos"></div>--%>
<%--    <div class="color tres"></div>--%>
<%--</div>--%>

<div class="inside-container primary">

    <c:if test="${not empty message}">
        <div class="notify">${message}</div>
    </c:if>
    <a href="${pageContext.request.contextPath}/prospects/${prospect.id}" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

    <h1>Add Notes</h1>
    <h3>${prospect.name}</h3>
    <c:set var="notes" value=""/>
    <c:if test="${prospect.notes != 'null'}">
        <c:set var="notes" value="${prospect.notes}"/>
    </c:if>
    <form action="${pageContext.request.contextPath}/prospects/notes/update/${prospect.id}" method="post">
        <textarea name="notes" placeholder="">${notes}</textarea>

        <div class="button-wrapper">
            <input type="submit" value="Update Notes" class="button green"/>
        </div>
    </form>
</div>
</body>
</html>
