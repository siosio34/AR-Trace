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
    
    4) you choose Python Server or Firebase Server In MainActivity
    
    - Firebase
    	ClientInstance.setInstanceClient("FIREBASE");
    - Python
      	ClientInstance.setInstanceClient("PYTHON");
# Quick Start - Server(HaProxy+Redis)

```
<install>
1. sudo apt-get install haproxy
2. wget http://download.redis.io/releases/redis-3.2.6.tar.gz
3. tar -xvf redis-3.2.6.tar.gz

ELK
4. sudo add-apt-repository -y ppa:webupd8team/java
5. sudo apt-get update
6. sudo apt-get -y install oracle-java8-installer
7. curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.1.2.tar.gz
8. curl -L -O https://artifacts.elastic.co/downloads/logstash/logstash-5.1.2.tar.gz
9. curl -L -O https://artifacts.elastic.co/downloads/kibana/kibana-5.1.2-linux-x86_64.tar.gz
10. tar -xvf elasticsearch-5.1.2.tar.gz
11. tar -xvf logstash-5.1.2.tar.gz
12. tar -xvf kibana-5.1.2-linux-x86_64.tar.gz

<run>
1) change haproxy configuration
sudo mv configure/haproxy.cfg /etc/haproxy/haproxy.cfg
sudo service haproxy restart
2) make redis server
./redis-3.2.6/utils/install_server.sh
if you want to access this redis by outside server,
change bind ip in /etc/redis/YOUR_REDIS_PORT.conf to 0.0.0.0

PATH_CLONE_AR-Trace(write your log redis ip and port)
in configure/logstash_haproxy.conf, configure/logstash_redis_to_es file )
3) ./elasticsearch-5.1.2/bin/elasticsearch
4) ./kibana-5.1.2-linux-x86_64/bin/kibana
5) ./logstash-5.1.2/bin/logstash -f /configure/logstash_haproxy.conf
6) ./logstash-5.1.2/bin/logstash -f /configure/logstash_redis_to_es.conf
```

# Quick Start - Server(Data, Image)

```
<install>
1. sudo apt-get install nginx
2. sudo pip install uwsgi
3. sudo apt-get install mysql-server
4. sudo apt-get install rabbitmq-server
5. sodu apt-get install mysql-python
6. sudo pip install virtualenv
7. sudo pip install flask
8. sudo pip install sqlalchemy
9. sudo pip install celery
10. sudo pip install eventlet
11. sudo pip install redis

logstash
12. sudo add-apt-repository -y ppa:webupd8team/java
13. sudo apt-get update
14. sudo apt-get -y install oracle-java8-installer
15. curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.1.2.tar.gz
16. tar -xvf elasticsearch-5.1.2.tar.gz

<run>
1) vi /etc/nginx/site-available/default add under code
server{
	listen YOUR_PORT;
	server_name YOUR_IP;
	location / {try_files $uri @app;}
	location @app{
		include uwsgi_params;
		uwsgi_pass unix:/tmp/uwsgi.sock;
	}
}
2) sudo service nginx restart

3) activate uwsgi
if mainserver
 sudo uwsgi configure/uwsgi.ini(
if subbserver
 sudo uwsgi configure/uwsgi2.ini
if image server
 sudo uwsgi configure/uwsgi_image.ini

4) sudo rabbitmq-plugins enable rabbitmq_management
5) sudo service rabbitmq-server restart
for monitoring rabbitmq
6) sudo rabbitmqctl add_user YOUR_USER_NAME YOUR_USER_PASSWORD
7) sudo rabbitmqctl set_user_tags YOUR_USER_NAME administrator

in /src/Server/tasks do under code for activate celery server
8) celery -A celery_handler worker --loglevel=info -E -P eventlet -c 1000

PATH_CLONE_AR-Trace (write your log redis ip and port in logstash_redis.conf file )
9) ./logstash-5.1.2/bin/logstash -f /configure/logstash_redis.conf
```



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

![](https://ww2.sinaimg.cn/large/006tKfTcgy1fd0ca2enuaj31660mutc4.jpg)

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

​    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or 
    (at your option) any later version.


    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.


    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>

​    




