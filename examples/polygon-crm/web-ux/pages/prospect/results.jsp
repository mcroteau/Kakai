<div class="inside-container">

    <a href="/prospects" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

    <br class="clear"/>

    <h2 id="results-h1">${prospects.size()} Results</h2>

    <br class="clear"/>

    <kakai:if spec="${prospects.size() > 0}">
        <table id="results">
            <kakai:iterate items="${prospects}" var="prospect" >
                <tr>
                    <td><a href="/prospects/${prospect.id}" style="text-decoration: none;">${prospect.name}</a></td>
                    <td class="center" style="width:190px;">${prospect.phone}</td>
                </tr>
            </kakai:iterate>
        </table>
    </kakai:if>

    <kakai:if spec="${prospects.size() == 0}">
        <p>No Prospects created yet.</p>
    </kakai:if>
</div>
</body>
</html>
