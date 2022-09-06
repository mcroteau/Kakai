<div class="inside-container">

    <a href="/prospects" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

    <kakai:if spec="${not empty message}">
        <div class="notify">${message}</div>
    </kakai:if>

    <h1>Add Prospect</h1>
    <p>Add a new prospect to your mining effort.</p>
    <form action="/prospects/save" method="post">

        <label>Name</label>
        <input type="text" name="name" style="width:100%;" />

        <label>Phone</label>
        <input type="text" name="phone" />

        <label>Status</label>
        <select name="status" style="display: block">
            <kakai:iterate items="${statuses}" var="status">
                <option value="${status.id}">${status.name}</option>
            </kakai:iterate>
        </select>


        <div class="button-wrapper-lonely">
            <input type="submit" class="button green" value="Save Prospect"/>
        </div>
    </form>
</div>
