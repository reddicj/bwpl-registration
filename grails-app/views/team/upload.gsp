<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${title}</title>
</head>
<body>
<div id="uploadRegistrations">
    <g:uploadForm action="uploadRegistrations" id="${team.id}">
        <input type="file" name="registrations" required="true"/>
        <g:submitButton name="upload" value="Upload" class="buttons"/>
    </g:uploadForm>
</div>
</body>
</html>