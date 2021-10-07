<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>GPMS v2.0</title>
</head>
<body>
<div id="root"></div>
<script>
  window.gpmsain = '10052017';

  window.roleClientAdmin = ${(empty ROLE_CLIENT_ADMIN) ? "0" : ROLE_CLIENT_ADMIN};
  window.roleUserAdmin = ${(empty ROLE_USER_ADMIN) ? "0" : ROLE_USER_ADMIN};
  window.roleDesktopAdmin = ${(empty ROLE_DESKTOP_ADMIN) ? "0" : ROLE_DESKTOP_ADMIN};
  window.roleNoticeAdmin = ${(empty ROLE_NOTICE_ADMIN) ? "0" : ROLE_NOTICE_ADMIN};
  window.rolePortableAdmin = ${(empty ROLE_PORTABLE_ADMIN) ? "0" : ROLE_PORTABLE_ADMIN};
  window.rolePortableUser = ${(empty ROLE_PORTABLE_USER) ? "0" : ROLE_PORTABLE_USER};

</script>
<script type="text/javascript" src="index.bundle.js"></script>
</body>
