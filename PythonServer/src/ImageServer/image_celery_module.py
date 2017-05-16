# -*-coding:utf-8
import os
import sys
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from celery import Celery


celery_app = Celery('image_celery_tasks', broker='amqp://', backend='amqp://')
celery_app.config_from_object('celery_config')
