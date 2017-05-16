# -*- coding: utf-8 -*-
from celery import Celery


celery_app = Celery('celery_handler', broker='amqp://', backend='amqp://')
celery_app.config_from_object('celery_config')
