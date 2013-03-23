<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>ASA Email</title>
</head>
<body>
<div class="bwpl-nav">
    <ul><li><a href="${email.mailToLink}" target="_blank">Create Email</a></li></ul>
</div>
<hr/>
<h2>${email.getSubject(false)}</h2>
<b>To: </b>${email.getTo(false)}
<hr/>
${email.getBody(false, "<br/>", "&nbsp;")}
</body>
</html>