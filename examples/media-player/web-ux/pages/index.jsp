<table>
    <kakai:iterate items="${music}" var="song">
        <tr>
            <td>${song.id}</td>
            <td>${song.title}</td>
            <td>${song.artist}</td>
            <td><audio src="/listen/${song.id}" controls></audio></td>
        </tr>
    </kakai:iterate>
</table>