<style>
    .akki-styles{
        height:73px;
    }
    label{
        display:block;
        margin:20px 0px 0px;
    }
    input[type="text"]{
    }
    .prospect-navigation{
        text-align: center;
        background: #f8f8f8;
        border-bottom: solid 1px #e6e8ea;
    }
    .prospect-navigation a{
        color:#000;
        font-size:21px;
        font-weight: 900;
        margin:10px 40px;
        text-decoration: none;
        display: inline-block;
    }
    /*.color{*/
    /*    height:70px;*/
    /*    margin-bottom:20px;*/
    /*    display: inline-block;*/
    /*}*/
    /*.color{*/
    /*    font-size:14px;*/
    /*    text-align: center;*/
    /*    text-decoration: none;*/
    /*}*/
    /*.color span{*/
    /*    color:#fff;*/
    /*    margin:26px auto;*/
    /*    display: inline-block;*/
    /*    font-weight: 900;*/
    /*}*/

    .inside-header{
        font-size:42px;
    }

</style>

<div class="inside-container primary" style="margin-top:-3px;">

    <kakai:if spec="${message != ''}">
        <div class="notify">${message}</div>
    </kakai:if>

    <div style="width:60%;float:left">
        <h1 class="inside-header">${prospect.name}</h1>
        <p style="font-size:32px;color:#4889f6">${prospect.phone}<br/>
            <span class="lightf">${prospect.email}</span></p>
    </div>

    <div style="width:40%;float:right;text-align: right;">

        <form action="/prospects/sale/${prospect.id}" method="post">
            <a href="/prospects/edit/${prospect.id}" class="button retro">Edit</a>
<%--            <kakai:if spec="${upcomingActivities.size() > 0}">--%>
<%--                <input type="submit" value="Add Sale $" class="button green"  onclick="return confirm('This will update all Sales Activities for this Prospect to Sale and get you ready for the next sales cycle!');"/>--%>
<%--            </kakai:if>--%>
        </form>

        <div style="margin-top:30px;">
            <p style="font-size:29px;font-weight:400;white-space: pre-line">${prospect.contacts}</p>
        </div>
    </div>
    <br class="clear"/>

</div>

<div style="border-bottom: dotted 1px #d0dde3;">
</div>

<div class="row-container">
    <div class="inside-container-row divider" style="width:50%;">
        <div class="upcoming-activities">
            <p><strong>Activities</strong></p>
            <kakai:if spec="${upcomingActivities.size() > 0}">
                <kakai:iterate items="${upcomingActivities}" var="activity">
                    <div style="margin-bottom:20px;padding-bottom:0px;">
                        <a href="/prospects/activity/edit/${activity.id}" class="sales-activity"
                           style="font-size:23px;display:block;margin-bottom:10px;">
                            <span class="activity-date href-dotted-black blue"><strong style="color:#000">${activity.prettyTime}</strong> : <strong style="color:#4889f6">${activity.name}</strong></span>
                        </a>
                        Reminders:
                        <kakai:if spec="${activity.fiveReminder}">
                            5 Min
                        </kakai:if>
                        <kakai:if spec="${activity.fifteenReminder}">
                            &nbsp;&nbsp;15 Min
                        </kakai:if>
                        <kakai:if spec="${activity.thirtyReminder}">
                            &nbsp;&nbsp;30 Min
                        </kakai:if>
<%--                        <kakai:if spec="${!activity.fiveReminder && !activity.fifteenReminder && !activity.thirtyReminder}">--%>
<%--                            <a href="${pageContext.request.contextPath}/prospects/activity/edit/${activity.id}" class="tiny href-dotted">Set Reminders</a>--%>
<%--                        </kakai:if>--%>
                        <br class="clear"/>
                    </div>
                </kakai:iterate>
                <p style="margin-top:50px;"></p>
                <kakai:if spec="${effort != null}">
                    <form action="/prospects/effort/stop/${prospect.id}" method="post">
                        <a href="/prospects/activity/add/${prospect.id}" class="href-dotted">!Add Activity</a>
                        <br/><br/>
<%--                        <input type="submit" value="Stop Effort : Running" class="no-effects" onclick="return confirm('Are you sure you want to stop sales effort?');"/>--%>
                        <br/>
                    </form>
                </kakai:if>

                <kakai:if spec="${effort == null}">
                    <form action="/prospects/effort/save/${prospect.id}" method="post">
                        <a href="/prospects/activity/add/${prospect.id}" class="href-dotted">Add Activity</a>
<%--                        &nbsp;&nbsp;<input type="submit" value="Start Effort!" class="no-effects"--%>
<%--                                           onclick="return confirm('Are you sure you want to begin sales effort?');"/>--%>
                    </form>
                </kakai:if>

            </kakai:if>
            <kakai:if spec="${upcomingActivities.size() == 0}">
                <p>No upcoming activities.
                <form action="/prospects/effort/save/${prospect.id}" method="post">
                    <a href="/prospects/activity/add/${prospect.id}" class="href-dotted">Add Activity</a>
<%--                    <input type="submit" value="Start Effort!" class="no-effects"--%>
<%--                           onclick="return confirm('Are you sure you want to begin sales effort?');"/>--%>
                </form>
                </p>
            </kakai:if>
        </div>
    </div>
    <div class="inside-container-row" style="vertical-align: top">
        <kakai:if spec="${prospect.notes != ''}">
            <div class="add-notes">
                <kakai:set var="notes" value=""/>
                <kakai:if spec="${prospect.notes != 'null'}">
                    <c:set var="notes" value="${prospect.notes}"/>
                </kakai:if>

                <p><strong>Notes</strong></p>
                <p>${notes}</p>
                <a href="${pageContext.request.contextPath}/prospects/notes/edit/${prospect.id}" class="href-dotted">Edit Notes</a>
            </div>
        </kakai:if>
        <kakai:if spec="${prospect.notes == ''}">
            <div class="add-notes">
                <p><strong>Notes</strong></p>
                <a href="${pageContext.request.contextPath}/prospects/notes/edit/${prospect.id}" class="button">Add Notes</a>
            </div>
        </kakai:if>
    </div>
</div>

<div class="bottom-divider"></div>

<div class="inside-container primary" style="margin-top:-3px;">
    <div class="button-wrapper-lonely">
        <form action="/prospects/delete/${prospect.id}" method="post">
            <input type="submit" class="button remove" value="Delete Prospect"/>
        </form>
    </div>
</div>