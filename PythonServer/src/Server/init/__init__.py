# -*- coding: utf-8 -*-
logger = open('/var/log/login.log', 'a')

from common.log_handler import LogHandler
from models.review import Review
from models.user import User
from init.mysql_db import engine
from init.redis_db import redis_engine
log_handler = LogHandler()

review = Review()
user = User()

