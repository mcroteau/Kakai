<div class="inside-container">

    <kakai:if spec="${message != ''}">
        <div class="notify">${message}</div>
    </kakai:if>
    <a href="/prospects/${activity.prospectId}" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

    <br class="clear"/>

    <h1 style="float:left;">Edit Sales Activity</h1>
    <form action="/prospects/activity/complete/${activity.id}" method="post" style="margin:20px 0px 0px 20px;float:left">
        <input type="submit" value="Mark as Complete &check;" class="button small sky" onclick="return confirm('Mark as completed?');"/>
    </form>
    <br class="clear"/>

    <form action="/prospects/activity/update/${activity.id}" method="post" id="edit-activity-form">
        <h3>${activity.name} @ ${activity.prettyTime}</h3>

        <label>Get Notified <span class="tiny">${activity.timeZone}</span></label>

        <kakai:set var="fiveChecked" val=""/>
        <kakai:if spec="${activity.fiveReminder}">
            <kakai:set var="fiveChecked" val="checked"/>
        </kakai:if>
        <kakaie:set var="fifteenChecked" val=""/>
        <kakai:if spec="${activity.fifteenReminder}">
            <kakai:set var="fifteenChecked" val="checked"/>
        </kakai:if>
        <kakai:set var="thirtyChecked" val=""/>
        <kakai:if spec="${activity.thirtyReminder}">
            <kakai:set var="thirtyChecked" val="checked"/>
        </kakai:if>
        <input type="checkbox" name="five" ${fiveChecked} onclick="submit()"/>5 Minute
        <input type="checkbox" name="fifteen" ${fifteenChecked} onclick="submit()"/>15 Minute
        <input type="checkbox" name="thirty" ${thirtyChecked} onclick="submit()"/>30 Minute


        <br/><br/>

        <label>Notification Phones</label>
        <p class="information">Phones need to be comma separated, start with a plus sign and country code with no spaces or special characters. Example : +19070000000</p>
        <textarea name="phones" placeholder="+18089192120,+19018910031">${activity.phones}</textarea>
        <br/>
        <br/>

        <div class="button-wrapper">
            <input type="submit" value="Update Activity" class="button green"/>
        </div>
    </form>

    <div style="margin-top:70px;">
        <form action="/prospects/activity/delete/${activity.id}" method="post">
            <input type="submit" value="Delete Activity" class="button remove" onclick="return confirm('Are you sure you want to delete sales activity?');" style="float:right"/>
            <br class="clear"/>
        </form>
    </div>
</div>

<script>
    var submit = function(){
        document.querySelector('#edit-activity-form').submit();
    }
</script>

