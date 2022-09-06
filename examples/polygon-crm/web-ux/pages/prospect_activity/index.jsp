<div class="inside-container">

    <a href="/prospects/${prospect.id}" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>


    <h1>${prospect.name}</h1>
    <h3>Add Sales Activity</h3>
    <form action="/prospects/activity/save/${prospect.id}" method="post">

        <label>Sales Activity</label>
        <select name="activity-id" style="display: block">
            <kakai:iterate items="${activities}" var="activity">
                <option value="${activity.id}">${activity.name}</option>
            </kakai:iterate>
        </select>

        <label>Sales Activity Date</label>
        <input type="date" id="date" name="activity-date"/>

        <label>Sales Activity Time : Expects 24 hour format.</label>
        <input type="number" name="activity-hour" maxlength="2" placeholder="23" style="width:105px;display:inline-block" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);"/>
        <input type="number" name="activity-minute" maxlength="2" placeholder="01" style="width:105px;" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);"/>

        <p class="information">Timezone: <span id="timezone-p"></span></p>

        <input type="hidden" name="timezone" id="timezone"/>

        <div class="button-wrapper-lonely">
            <input type="submit" value="Add Activity" class="button green"/>
        </div>
    </form>
</div>

<script>
    var date = new Date();
    var d = date.toDateString();
    document.querySelector('#date').valueAsDate = new Date();
    const tzid = Intl.DateTimeFormat().resolvedOptions().timeZone;
    document.querySelector('#timezone').value = tzid;
    document.querySelector('#timezone-p').innerHTML = tzid;
</script>
