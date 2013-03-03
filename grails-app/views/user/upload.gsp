<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${title}</title>
</head>
<body>
<div>
    <g:uploadForm action="uploadUsers">
        <input type="file" name="users" required="true"/>
        <g:submitButton name="upload" value="Upload" class="buttons"/>
    </g:uploadForm>
</div>
</body>
</html>