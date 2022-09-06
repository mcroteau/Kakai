<h3>Upload Music</h3>
<form action="/upload" method="post" enctype="multipart/form-data">
    <label>Title</label>
    <input type="text" name="title" value=""/>
    <label>Artist</label>
    <input type="text" name="artist" value=""/>

    <lable>Song</lable>
    <input type="file" name="media" value=""/>

    <input type="submit" value="Upload"/>
</form>