<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${title}</title>
</head>
<body>
<div id="uploadClubs">
<g:uploadForm action="uploadClubData">
    <input type="file" name="clubs" />
    <g:submitButton name="upload" value="Upload" class="buttons"/>
</g:uploadForm>
</div>
</body>
</html>