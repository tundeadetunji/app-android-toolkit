# Remote Linq
<br>
<p>
From your android phone/tablet, on the tap of a button, you can
</p>
<li>start playing music on</li>
<li>shut down</li>
<li>restart</li>
<li>hibernate</li>
<li>lock</li>
<li>run any program on</li>
<li>open any file on</li>
<br>
<p>
your Windows computer from anywhere, as long as there's internet, completely remotely and unobtrusively, the mouse pointer doesn't even move. I found equivalents to this app, but what led me to try this out was to have the feature which I found lacking in the equivalents - I don't want the user in front of the PC to feel like I'm controlling the machine.😎
</p>
<p>
The PC listens for commands which are sent to a web server (running ASP.NET) via HTTP request from the android device. The request is stored in the database temporarily, and the entry is deleted when the PC has begun the action. Each request is targeted to a particular PC via its
name, and the user on the android has to log in with an email address to send any request, if the PC is not paired with this email, it won't respond.
</p>
<p>
  You can play media, send commands, turn on caps lock/numlock etc, interact with other programs, create/update files etc. Mostly Command Pattern + Chain Of Responsibility at work.
</p>

<br>
<h3>Runs on</h3>
Android + Windows PC
<br>
<br>
