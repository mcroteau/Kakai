    <div class="inside-container">

        <a href="/prospects/${prospect.id}" id="prospect-back" class="href-dotted">&larr;&nbsp;Back</a>

        <kakai:if spec="${message != ''}">
            <div class="notify">${message}</div>
        </kakai:if>

        <h1>Edit Prospect</h1>

        <form action="/prospects/update" method="post">

            <input type="hidden" name="id" value="${prospect.id}"/>

            <label>Name</label>
            <input type="text" name="name" value="${prospect.name}" style="width:90%;"/>

            <label>Phone</label>
            <input type="text" name="phone" value="${prospect.phone}" style="width:70%"/>

            <label>Email</label>
            <input type="text" name="email" value="${prospect.email}" style="width:100%"/>

            <label>Status</label>
            <select name="statusId" style="display: block">
                <kakai:iterate items="${statuses}" var="status">
                    <kakai:set var="selected" val=""/>
                    <kakai:if spec="${status.id == prospect.statusId}">
                        <kakai:set var="selected" val="selected"/>
                    </kakai:if>
                    <option value="${status.id}" ${selected}>${status.name}</option>
                </kakai:iterate>
            </select>

            <label>Contacts</label>
            <textarea name="contacts" placeholder="Manny Paquito (820) 291-1235">${prospect.contacts}</textarea>

            <div class="button-wrapper-lonely">
                <input type="submit" class="button green" value="Update Prospect"/>
            </div>
        </form>
    </div>
