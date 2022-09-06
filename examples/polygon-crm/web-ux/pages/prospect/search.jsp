
    <div class="inside-container">

        <kakai:if spec="${message != ''}">
            <div class="notify">${message}</div>
        </kakai:if>

        <form action="/prospects/search" method="get">
            <h2 class="left-float">Search Prospects: </h2>

            <br class="clear"/>

            <p>${prospects.size()} Prospects</p>

            <input type="text" name="q" placeholder="Blue Water Trucking Co." id="prospect-search"/>

            <div class="button-wrapper-tiny">
            <input type="submit" value="Search!" class="button super green" id="search-button" style="float: right"/>
            <br class="clear"/>
            </div>
        </form>

        <kakai:if spec="${prospects.size() > 0}">
            <table id="results">
                <kakai:iterate items="${prospects}" var="prospect">
                    <tr>
                        <td><a href="/prospects/${prospect.id}" style="text-decoration: none;">${prospect.name}</a></td>
                        <td class="center" style="width:190px;">${prospect.phone}</td>
                    </tr>
                </kakai:iterate>
            </table>
        </kakai:if>

        <kakai:if spec="${prospects.size() == 0}">
            <p class="notify">No Prospects created yet.</p>
        </kakai:if>

    </div>

</body>
</html>
