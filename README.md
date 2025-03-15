# Remote Linq

![SOS](https://github.com/user-attachments/assets/cb16c31a-d4d3-4e05-9a11-5992069f36fd)

When you long-tap on the image, it DISCREETLY starts sending your current location updates to a server at home

![GITHUB](https://github.com/user-attachments/assets/f22f2935-49ca-497d-ba37-948a95bb1603)

You can create/read GitHub repos, and more

![ESP](https://github.com/user-attachments/assets/8b792387-cecc-4339-aa2a-d73aee0ddb89)

For ESP Home services, if installed, the app can create config file and place it wherever you want on your server system

![SCHEDULER](https://github.com/user-attachments/assets/a490184b-22d5-4626-a9b0-51bd8249386f)

You can initiate a task schedule on your ocmputer. Say, run a script file on Mondays at 9.00 AM.

![COMMAND](https://github.com/user-attachments/assets/9fed0d70-83f4-4fdf-8ab6-562c53992afe)

You can send commands to your PC



<br>
<br>

## How it works:
Desktop Client (.NET) constantly checks Web Server (.NET) for commands meant for this computer (if the name maches). It can also send command to another PC, group of PCs or Android Client.

Web Server (.NET) listens for commands. If a command comes in, it stores it (SQL). Command can come from Desktop Client or Android Client.

Android Client sends command meant for a particular PC or group of PCs. It can also check Web Server for any command meant for it.
