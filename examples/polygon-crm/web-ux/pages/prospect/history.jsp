<div class="inside-container">

    <kakai:if spec="${message != ''}">
        <div class="notify">${message}</div>
    </kakai:if>
    <a href="/prospects/${prospect.id}" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

    <h1>${prospect.name}</h1>
    <p>Sales Activity History</p>

    <kakai:if spec="${activities.size() > 0}">
        <kakai:iterate items="${activities}" var="activity">
            <div style="border-bottom:solid 1px #ccc;margin-bottom:20px;padding-bottom:10px;" class="href-dotted-black">
                <div class="sales-activity"
                     style="font-size:23px;display:block;margin-bottom:10px;">
                    <span class="activity-date"><strong>${activity.prettyTime}</strong> : ${activity.name} &check;</span>
                </div>
            </div>
        </kakai:iterate>
    </kakai:if>

    <kakai:if spec="${activities.size() == 0}">
        <p>No completed activities yet.</p>
    </kakai:if>
</div>

</body>
</html>
