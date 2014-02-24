The intention of the CoFH Common Library is to establish a framework for 
modders to use to avoid "reinventing the wheel," as it were. This 
library contains a lot of basic functionality that quite a few mods end 
up adding at some point during their development. 

Ideally, using this library will allow for more rapid mod development as 
a lot of the "boring" yet essential code is provided here. 

IMPORTANT:
Here's the catch: you should *shadow* these files if at all 
possible. That means you should NOT directly reference these. Copy 
functions, methods, whatever, but do NOT directly use them or include 
them unless the files are in the /api package. If you do so, and we 
update in the future, and you are responsible for crashing any CoFH mod 
as a result, we will very casually and without further warning blacklist 
any of our mods from working with yours. This is a necessary precaution 
on our end, sorry. 

So, you have been warned. Make use of the stuff in here, have fun with 
it, but do *not* directly reference it unless you are comfortable 
requiring CoFH Core to be installed. If you ship anything other than 
/api with your mods, we are going to have problems. 

Obviously, we have to maintain this library and keep it compatible with 
the changes and updates introduced by Minecraft and Forge, but we'll try 
and only add new things if at all possible. 

If you feel that there is anything obvious or very basic that we have 
missed, feel free to suggest it to the team. 

We look forward to seeing what the community does with this. 

-Team CoFH 

Oh, and don't be a jerk. 

