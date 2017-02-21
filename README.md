# AR-Trace

    This Project AR Search Place, Facilities And Navigating where you want to go by using Naver API.

    Also you leave Trace and see other people's trace in for Places(In a word Place Based SNS)

    Client select Server environmet Firebase and Python Server.

    we want this Project will be good reference to someone for develop Production level service or server-client Project. 
    
# Qucik Start - Client

    Android App Client

    1) Install Android-Studio : https://developer.android.com/studio/index.html?hl=ko

    2) File -> Import Existing Project -> PATH_CLONE_AR-Trace/AR-Trace

    3) Firebase Setting - Download goole-services.json in your Project

    - Create FireBase Project - https://console.firebase.google.com/

    - FireBase Android Setting - https://firebase.google.com/docs/android/setup

    - Download google-services.json in your Project
    
# Qucik Start - Server    
  
-  <b>Firebase Server Setting - if you don't have server or you want to use Firebase Server 
    
        1) Create FireBase Project - https://console.firebase.google.com/

        2) FireBase Android Setting - https://firebase.google.com/docs/android/setup
    
        3) Download google-services.json in your Project
        
     
-  <b>Python Server Setting - if you have server or you want to use python Server
        
        but this Project in Processing... so you want to contribute our project 
        then you set Firebase Server Setting now...!

        ...todo Processing...
        
# Qucik Start - Server(Push)

    -> In Ubuntu Setting...! 
    
    1. sudo apt-get install httpd
    2. sudo apt-get install php
    3. mv PATH_CLONE_AR-Trace/AR-Trace/PushServer/PushServer.php /var/www/html/PushServer.php
    
    -> In Client Setting...!
    
    1. PATH_CLONE_AR-Trace/AR-Trace/app/src/main/res/values/strings.xml file change to your IP
      
    <!-- Push server IP -->
    <string name="PUSH_SERVER_IP">YOUR_PUSH SERVER_IP</string>
    

- Google Cloud Messaging Will be deprecated so we use Firebase Cloud Messaging(FCM) for app Notify
- Firebase Cloud Messging Only Support Downstram message and you want to develop all function of Push you must have to App Server Setting

# Client & Firebase Server Structure

![](https://ww1.sinaimg.cn/large/006tKfTcgy1fcmvrstxewj31a40uw76r.jpg)

# Client & Python Server Structure

![](https://ww1.sinaimg.cn/large/006tKfTcgy1fcmwr9sk33j31680o2whb.jpg)

# Intro Video
- not new version Video we will new version upload In future

[![IMAGE ALT youtube](http://img.youtube.com/vi/V0eGnEXL0VQ/0.jpg)](http://www.youtube.com/watch?v=V0eGnEXL0VQ)

# References 
- mixare - <http://www.mixare.org/>
- naver API - <https://developers.naver.com/>
- typekit - <https://github.com/tsengvn/typekit>
- Firebase RealTime database - <https://firebase.google.com/docs/database/>
- Firebase RealTime storage - <https://firebase.google.com/docs/storage/>
- Firebase auth - <https://firebase.google.com/docs/auth/>
- Firebase cloud messging - <https://firebase.google.com/docs/cloud-messaging/>
- Picasso - <https://github.com/square/picasso>
- Picasso-trasform - <https://github.com/wasabeef/picasso-transformations>
- gson - <https://github.com/google/gson>
- Flask - <http://flask-docs-kr.readthedocs.io/>
- celery - <http://www.celeryproject.org/>
- celery-flower - <http://flower.readthedocs.io/>
- rabbitmq - <https://www.rabbitmq.com/>
- eventlet - <http://eventlet.net/>
- nginx - <https://www.nginx.com/>
- uwsgi : https://uwsgi-docs.readthedocs.io/
- redis-py : https://redis-py.readthedocs.io/
- haproxy : http://www.haproxy.org/
- logstash : https://www.elastic.co/kr/products/logstash
- elasticsearch : https://www.elastic.co/kr/
- kibana : https://www.elastic.co/kr/products/kibana
- sqlalchemy : http://www.sqlalchemy.org/
- MySQL-Python : http://mysql-python.sourceforge.net/
- Virtualenv : https://virtualenv.pypa.io
- Redis : https://redis.io/


# Copyright
![alt AR-Trace](https://github.com/siosio34/AR-Trace/blob/master/docs/ar_trace_profile.png)

    Youngje jo [ siosio34@nate.com ] site : https://github.com/siosio34
    Mansu KIM [ akakakakakaa@gmail.com ] site : https://github.com/akakakakakaa
    Jihyun KIM [ wlgus0078@naver.com ] site : https://github.com/KIMgsu
    
# Lisence
    GPL v3 Copyright (C) {2017} {@siosio34, @akakakakakaa, @KIMgsu}

    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or 
    (at your option) any later version.


    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.


    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>
    
    




